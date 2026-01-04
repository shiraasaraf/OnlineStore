/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Category;
import store.products.Product;

import store.cart.CartItem;
import store.gui.util.WindowWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main store window.
 * <p>
 * Displays the product catalog grid, product details panel, and shopping cart panel.
 * Includes search and category filtering.
 * </p>
 *
 * <p>
 * UI rule:
 * - Filters (Search + Category) are always visible for both Customer and Manager.
 * - Actions buttons are role-based: Customer sees only customer actions;
 *   Manager sees manager actions.
 * </p>
 *
 * <p>
 * Catalog rule:
 * - Shows ALL products (including stock = 0).
 * </p>
 */
public class StoreWindow extends JFrame {

    private final WindowWorker worker;

    /** Catalog grid panel. */
    private final JPanel catalogPanel;

    /** Controller used to access model operations. */
    private final StoreController controller;

    /** Panel that displays selected product details. */
    private final ProductDetailsPanel detailsPanel;

    /** Shopping cart panel. */
    private final CartPanel cartPanel;

    // --- Actions buttons (some appear only for manager UI) ---
    private final JButton loadButton = new JButton("Load");
    private final JButton saveButton = new JButton("Save");
    private final JButton manageCatalogButton = new JButton("Manage Catalog");
    private final JButton historyButton = new JButton("Order History");

    /** Search field. */
    private final JTextField searchField = new JTextField(18);

    /** Clears search and filters. */
    private final JButton clearSearchButton = new JButton("Clear");

    /** Category filter combo box ("All" + categories). */
    private final JComboBox<Object> categoryCombo = new JComboBox<>();

    /**
     * Constructs the main store window.
     *
     * @param storeController store controller
     */
    public StoreWindow(StoreController storeController) {

        this.controller = storeController;

        String roleName = controller.canManage() ? "Manager" : "Customer";
        this.worker = new WindowWorker(roleName + "-WindowWorker-" + System.identityHashCode(this));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                worker.close();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setTitle("Online Store (" + roleName + ")");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(10, 10));

        // NORTH: Title + Filters (always) + Actions (role-based)
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.add(new JLabel("Product Catalog"), BorderLayout.WEST);

        topBar.add(buildFiltersPanel(), BorderLayout.CENTER);
        topBar.add(buildActionsPanel(), BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // CENTER: Catalog
        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);

        // EAST: Details + Cart
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        detailsPanel = new ProductDetailsPanel(null);
        cartPanel = new CartPanel();

        rightPanel.add(detailsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(cartPanel);

        add(rightPanel, BorderLayout.EAST);

        // Wire listeners (same logic you had, only adjusted to getAllProducts())
        wireActions();
        wireCartAndDetails();

        // Initial load / filters
        refreshFiltersAfterCatalogChange();
    }

    // -------------------------------------------------------------------------
    // UI Builders
    // -------------------------------------------------------------------------

    /** Builds the filters panel (always visible). */
    private JPanel buildFiltersPanel() {
        JPanel filtersBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filtersBar.add(new JLabel("Search:"));
        filtersBar.add(searchField);
        filtersBar.add(clearSearchButton);

        filtersBar.add(Box.createHorizontalStrut(15));
        filtersBar.add(new JLabel("Category:"));
        filtersBar.add(categoryCombo);

        // Live search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void apply() { applyFilters(); }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { apply(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { apply(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { apply(); }
        });

        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            applyFilters();
        });

        categoryCombo.addActionListener(e -> applyFilters());

        return filtersBar;
    }

    /** Builds the actions panel based on role. */
    private JPanel buildActionsPanel() {
        return controller.canManage()
                ? buildManagerActionsPanel()
                : buildCustomerActionsPanel();
    }

    private JPanel buildCustomerActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(historyButton);
        return p;
    }

    private JPanel buildManagerActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(loadButton);
        p.add(saveButton);
        p.add(manageCatalogButton);
        p.add(historyButton);
        return p;
    }

    // -------------------------------------------------------------------------
    // Wiring
    // -------------------------------------------------------------------------

    private void wireActions() {
        // Load products (manager only button exists only in manager UI)
        loadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File selectedFile = chooser.getSelectedFile();

            worker.runAsync(
                    () -> {
                        try {
                            controller.loadProductsFromFile(selectedFile);
                            return controller.getAllProducts();
                        } catch (IOException ex) {
                            throw new RuntimeException("Failed to load products from file", ex);
                        }
                    },
                    products -> {
                        setCatalogProducts(products);
                        refreshFiltersAfterCatalogChange();
                        JOptionPane.showMessageDialog(
                                this,
                                "Products loaded successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    },
                    ex -> JOptionPane.showMessageDialog(
                            this,
                            "Failed to load products from file:\n" + ex.getMessage(),
                            "IO Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        });

        // Save products (manager only)
        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File selectedFile = chooser.getSelectedFile();

            worker.runAsync(
                    () -> {
                        try {
                            controller.saveProductsToFile(selectedFile);
                        } catch (IOException ex) {
                            throw new RuntimeException("Failed to save products to file", ex);
                        }
                    },
                    () -> JOptionPane.showMessageDialog(
                            this,
                            "Products saved successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    ),
                    ex -> JOptionPane.showMessageDialog(
                            this,
                            "Failed to save products to file:\n" + ex.getMessage(),
                            "IO Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        });

        // Manage catalog (manager only)
        manageCatalogButton.addActionListener(e -> {
            CatalogManagementWindow dialog = new CatalogManagementWindow(this, controller);
            dialog.setVisible(true);

            setCatalogProducts(controller.getAllProducts());
            refreshFiltersAfterCatalogChange();
        });

        // Order history (both)
        historyButton.addActionListener(e -> {
            OrderHistoryWindow dialog = new OrderHistoryWindow(this, controller);
            dialog.setVisible(true);
        });
    }

    private void wireCartAndDetails() {
        cartPanel.addRemoveItemListener(ev -> {
            JButton btn = (JButton) ev.getSource();
            Product p = (Product) btn.getClientProperty("product");
            if (p == null) return;

            worker.runAsync(
                    () -> {
                        boolean removed = controller.removeFromCart(p);
                        return new UiSnapshot(removed, controller.getItems(), controller.getAllProducts());
                    },
                    snapshot -> {
                        if (!snapshot.success) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Failed to remove item from cart.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        cartPanel.setItems(snapshot.items);
                        setCatalogProducts(snapshot.products);
                        detailsPanel.setProduct(detailsPanel.getProduct());
                        applyFilters();
                    },
                    ex -> JOptionPane.showMessageDialog(
                            this,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        });

        cartPanel.addCheckoutListener(e -> {
            worker.runAsync(
                    () -> {
                        boolean ok = controller.checkout();
                        return new UiSnapshot(ok, controller.getItems(), controller.getAllProducts());
                    },
                    snapshot -> {
                        cartPanel.setItems(snapshot.items);
                        setCatalogProducts(snapshot.products);
                        detailsPanel.setProduct(detailsPanel.getProduct());
                        applyFilters();

                        if (snapshot.success) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Order completed successfully!",
                                    "Checkout",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Checkout failed (cart empty or stock changed).",
                                    "Checkout",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                    },
                    ex -> JOptionPane.showMessageDialog(
                            this,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        });

        detailsPanel.addAddToCartListener(e -> {
            Product p = detailsPanel.getProduct();
            if (p == null) return;

            int quantity = 1;
            worker.runAsync(
                    () -> {
                        boolean added = controller.addToCart(p, quantity);
                        return new UiSnapshot(added, controller.getItems(), controller.getAllProducts());
                    },
                    snapshot -> {
                        cartPanel.setItems(snapshot.items);
                        setCatalogProducts(snapshot.products);
                        detailsPanel.setProduct(p);
                        applyFilters();

                        if (snapshot.success) {
                            detailsPanel.showAddedFeedback();
                        } else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Could not add product to cart (maybe out of stock).",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    },
                    ex -> JOptionPane.showMessageDialog(
                            this,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        });
    }

    // -------------------------------------------------------------------------
    // Catalog + Filters
    // -------------------------------------------------------------------------

    /**
     * Sets the catalog products and rebuilds the grid UI.
     *
     * @param products products to display
     */
    public void setCatalogProducts(List<Product> products) {
        catalogPanel.removeAll();

        for (Product p : products) {
            ProductPanel panel = new ProductPanel(p);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    detailsPanel.setProduct(p);
                }
            });

            catalogPanel.add(panel);
        }

        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    /**
     * Rebuilds the category combo box from the given products.
     *
     * @param products products list
     */
    private void rebuildCategoryCombo(List<Product> products) {
        categoryCombo.removeAllItems();
        categoryCombo.addItem("All");

        java.util.Set<Category> categories = new java.util.TreeSet<>(
                java.util.Comparator.comparing(Enum::name)
        );

        for (Product p : products) {
            if (p == null || p.getCategory() == null) continue;
            categories.add(p.getCategory());
        }

        for (Category c : categories) {
            categoryCombo.addItem(c);
        }
    }

    /**
     * Applies search and category filters and refreshes the catalog grid.
     */
    private void applyFilters() {
        String text = (searchField.getText() == null)
                ? ""
                : searchField.getText().trim().toLowerCase();

        Object selected = categoryCombo.getSelectedItem();

        List<Product> all = controller.getAllProducts();
        java.util.List<Product> filtered = new java.util.ArrayList<>();

        for (Product p : all) {
            if (p == null) continue;

            String name = (p.getName() == null) ? "" : p.getName();

            boolean matchName = text.isEmpty() || name.toLowerCase().contains(text);

            boolean matchCategory =
                    selected == null ||
                            "All".equals(selected) ||
                            p.getCategory() == selected;

            if (matchName && matchCategory) {
                filtered.add(p);
            }
        }

        setCatalogProducts(filtered);
    }

    /**
     * Refreshes category options and applies the current filters.
     */
    private void refreshFiltersAfterCatalogChange() {
        rebuildCategoryCombo(controller.getAllProducts());
        applyFilters();
    }

    // -------------------------------------------------------------------------
    // UiSnapshot
    // -------------------------------------------------------------------------

    private static final class UiSnapshot {
        private final boolean success;
        private final java.util.List<CartItem> items;
        private final java.util.List<Product> products;

        private UiSnapshot(boolean success, java.util.List<CartItem> items, java.util.List<Product> products) {
            this.success = success;
            this.items = items;
            this.products = products;
        }
    }
}
