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
 * Includes search and category filtering, and provides manager actions (load/save/manage catalog).
 * </p>
 *
 * <p>
 * This version shows ALL products in the catalog (including stock = 0).
 * Products that are out of stock remain visible (UX-friendly).
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

    /** Button for loading products (manager only). */
    private final JButton loadButton;

    /** Button for saving products (manager only). */
    private final JButton saveButton;

    /** Button for opening catalog management (manager only). */
    private final JButton manageCatalogButton;

    /** Button for opening order history. */
    private final JButton historyButton;

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

        setTitle("Online Store");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(10, 10));

        // Top bar: title + buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(new JLabel("Product Catalog"), BorderLayout.WEST);

        JPanel ioButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loadButton = new JButton("Loading products from a file");
        saveButton = new JButton("Saving products to a file");
        manageCatalogButton = new JButton("Manage catalog");
        historyButton = new JButton("Order History");

        boolean isManager = controller.canManage();
        manageCatalogButton.setEnabled(isManager);
        loadButton.setEnabled(isManager);
        saveButton.setEnabled(isManager);

        historyButton.setEnabled(true);

        // Load products
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
                        rebuildCategoryCombo(controller.getAllProducts());
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

        // Save products
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

        // Manage catalog
        manageCatalogButton.addActionListener(e -> {
            CatalogManagementWindow dialog = new CatalogManagementWindow(this, controller);
            dialog.setVisible(true);
            setCatalogProducts(controller.getAllProducts());
            refreshFiltersAfterCatalogChange();
        });

        // Order history
        historyButton.addActionListener(e -> {
            OrderHistoryWindow dialog = new OrderHistoryWindow(this, controller);
            dialog.setVisible(true);
        });

        ioButtons.add(loadButton);
        ioButtons.add(saveButton);
        ioButtons.add(manageCatalogButton);
        ioButtons.add(historyButton);

        topBar.add(ioButtons, BorderLayout.EAST);

        // Search + Category Filter bar
        JPanel filtersBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filtersBar.add(new JLabel("Search:"));
        filtersBar.add(searchField);
        filtersBar.add(clearSearchButton);

        filtersBar.add(Box.createHorizontalStrut(15));
        filtersBar.add(new JLabel("Category:"));
        filtersBar.add(categoryCombo);

        topBar.add(filtersBar, BorderLayout.CENTER);

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

        add(topBar, BorderLayout.NORTH);

        // Catalog Center
        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);

        // Right side: details + cart
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        detailsPanel = new ProductDetailsPanel(null);
        cartPanel = new CartPanel();

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
                        applyFilters(); // keep current filters applied
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
                        applyFilters(); // re-apply filters after stock changed

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

        rightPanel.add(detailsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(cartPanel);

        add(rightPanel, BorderLayout.EAST);

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
                        applyFilters(); // keep current filters applied

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

        refreshFiltersAfterCatalogChange();
    }

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
