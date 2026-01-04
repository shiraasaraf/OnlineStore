/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.cart.CartItem;
import store.gui.controller.StoreController;
import store.gui.util.WindowWorker;
import store.products.Category;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main store window for displaying and interacting with the product catalog.
 * <p>
 * This window is role-aware:
 * </p>
 * <ul>
 *   <li><b>Customer</b>: sees the catalog grid and also a right-side panel containing
 *       {@link ProductDetailsPanel} and {@link CartPanel}.</li>
 *   <li><b>Manager</b>: sees the catalog grid and manager actions (load/save/manage catalog),
 *       but does not see the right-side customer panel.</li>
 * </ul>
 *
 * <p>
 * UI behavior rules:
 * </p>
 * <ul>
 *   <li>Filters (Search + Category) are always visible for both Customer and Manager.</li>
 *   <li>Action buttons are role-based: customers see only customer actions, managers see manager actions.</li>
 *   <li>The catalog grid shows all products, including products with stock {@code 0}.</li>
 * </ul>
 *
 * <p>
 * Threading:
 * Potentially slow operations (file IO / checkout operations) are executed asynchronously using
 * {@link WindowWorker} to keep the Swing Event Dispatch Thread responsive.
 * </p>
 */
public class StoreWindow extends JFrame {

    /** Background worker used to run slow tasks off the Swing EDT. */
    private final WindowWorker worker;

    /** Catalog grid panel (center area). */
    private final JPanel catalogPanel;

    /** Controller used to access model operations. */
    private final StoreController controller;

    /** Panel that displays selected product details (customer only; {@code null} for manager). */
    private ProductDetailsPanel detailsPanel;

    /** Shopping cart panel (customer only; {@code null} for manager). */
    private CartPanel cartPanel;

    // --- Action buttons (some appear only for manager UI) ---
    /** Loads products from a file (manager only). */
    private final JButton loadButton = new JButton("Load");

    /** Saves products to a file (manager only). */
    private final JButton saveButton = new JButton("Save");

    /** Opens the catalog management dialog (manager only). */
    private final JButton manageCatalogButton = new JButton("Manage Catalog");

    /** Opens the order history dialog (both customer and manager). */
    private final JButton historyButton = new JButton("Order History");

    /** Search input field (always visible). */
    private final JTextField searchField = new JTextField(18);

    /** Clears search and category filters (always visible). */
    private final JButton clearSearchButton = new JButton("Clear");

    /** Category filter combo box ("All" + categories) (always visible). */
    private final JComboBox<Object> categoryCombo = new JComboBox<>();

    /**
     * Constructs the main store window.
     * <p>
     * The window layout includes a top bar with a title, filters, and role-based action buttons,
     * a center catalog grid, and (for customers only) a right-side panel with product details and cart.
     * </p>
     * <p>
     * A dedicated {@link WindowWorker} is created per window instance and closed when the window closes.
     * </p>
     *
     * @param storeController the store controller used by this window
     * @throws IllegalArgumentException if {@code storeController} is {@code null}
     */
    public StoreWindow(StoreController storeController) {
        if (storeController == null) {
            throw new IllegalArgumentException("storeController cannot be null");
        }

        this.controller = storeController;

        String roleName = controller.canManage() ? "Manager" : "Customer";
        this.worker = new WindowWorker(roleName + "-WindowWorker-" + System.identityHashCode(this));

        /**
         * Ensures resources are released when the window is closed.
         * Closes the associated {@link WindowWorker} and disposes the frame.
         */
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

        // NORTH: Title (centered) + Filters (below title) + Actions (right)
        JPanel topBar = new JPanel(new BorderLayout(10, 10));

        JPanel centerBlock = new JPanel();
        centerBlock.setLayout(new BoxLayout(centerBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Products Catalog", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        centerBlock.add(titleLabel);
        centerBlock.add(Box.createVerticalStrut(8));

        JPanel filtersPanel = buildFiltersPanel();
        filtersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBlock.add(filtersPanel);

        topBar.add(centerBlock, BorderLayout.CENTER);
        topBar.add(buildActionsPanel(), BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // CENTER: Catalog
        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);

        // EAST: Details + Cart (CUSTOMER ONLY)
        if (!controller.canManage()) {
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

            detailsPanel = new ProductDetailsPanel(null);
            cartPanel = new CartPanel();

            rightPanel.add(detailsPanel);
            rightPanel.add(Box.createVerticalStrut(10));
            rightPanel.add(cartPanel);

            add(rightPanel, BorderLayout.EAST);
        } else {
            // Manager: no right-side panel
            detailsPanel = null;
            cartPanel = null;
        }

        // Wire actions (always)
        wireActions();

        // Wire customer-only cart/details listeners
        if (!controller.canManage()) {
            wireCartAndDetails();
        }

        // Initial load / filters
        refreshCatalogView();
    }

    // -------------------------------------------------------------------------
    // UI Builders
    // -------------------------------------------------------------------------

    /**
     * Builds the filters panel (always visible).
     * <p>
     * Includes a live search field, a clear button, and a category combo box.
     * Any change triggers {@link #applyFilters()} to refresh the catalog grid.
     * </p>
     *
     * @return a configured filters panel
     */
    private JPanel buildFiltersPanel() {
        JPanel filtersBar = new JPanel(new FlowLayout(FlowLayout.CENTER));

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

    /**
     * Builds the actions panel based on the current user's role.
     * <p>
     * Managers receive Load/Save/Manage Catalog + Order History.
     * Customers receive only Order History.
     * </p>
     *
     * @return the role-specific actions panel
     */
    private JPanel buildActionsPanel() {
        return controller.canManage()
                ? buildManagerActionsPanel()
                : buildCustomerActionsPanel();
    }

    /**
     * Builds the customer actions panel.
     *
     * @return a panel containing customer actions
     */
    private JPanel buildCustomerActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(historyButton);
        return p;
    }

    /**
     * Builds the manager actions panel.
     *
     * @return a panel containing manager actions
     */
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

    /**
     * Wires action buttons to their handlers.
     * <p>
     * File operations (load/save) are executed asynchronously via {@link WindowWorker}
     * to avoid blocking the Swing EDT. After successful load/save, the UI is updated
     * and a status dialog is shown.
     * </p>
     */
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
                            return null;
                        } catch (IOException ex) {
                            throw new RuntimeException("Failed to load products from file", ex);
                        }
                    },
                    ignored -> {
                        refreshCatalogView();
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
                            return null;
                        } catch (IOException ex) {
                            throw new RuntimeException("Failed to save products to file", ex);
                        }
                    },
                    ignored -> JOptionPane.showMessageDialog(
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
            refreshCatalogView();
        });

        // Order history (both)
        historyButton.addActionListener(e -> {
            OrderHistoryWindow dialog = new OrderHistoryWindow(this, controller);
            dialog.setVisible(true);
        });
    }

    /**
     * Wires customer-only interactions for cart and product details.
     * <p>
     * Handles:
     * </p>
     * <ul>
     *   <li>Removing items from the cart</li>
     *   <li>Checkout</li>
     *   <li>Adding the selected product to the cart</li>
     * </ul>
     * <p>
     * Each operation runs asynchronously using {@link WindowWorker} and then updates
     * the UI using a {@link UiSnapshot} returned from the background task.
     * </p>
     */
    private void wireCartAndDetails() {
        if (cartPanel == null || detailsPanel == null) return;

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
     * Sets the catalog products and rebuilds the catalog grid UI.
     * <p>
     * Each product is shown using a {@link ProductPanel}. Clicking a product panel
     * updates the {@link ProductDetailsPanel} (customer only).
     * </p>
     *
     * @param products products to display
     */
    public void setCatalogProducts(List<Product> products) {
        catalogPanel.removeAll();

        for (Product p : products) {
            ProductPanel panel = new ProductPanel(p);

            /**
             * When a product is clicked, show its details (customer only).
             */
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (detailsPanel != null) { // customer only
                        detailsPanel.setProduct(p);
                    }
                }
            });

            catalogPanel.add(panel);
        }

        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    /**
     * Rebuilds the category combo box from the given products.
     * <p>
     * Always includes an "All" option as the first entry.
     * Categories are collected from the products list and displayed in alphabetical order.
     * </p>
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
     * <p>
     * Search is performed as a case-insensitive substring match on the product name.
     * Category matches either "All" or the exact selected {@link Category}.
     * </p>
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
     * Refreshes category options and then applies the current filters.
     * <p>
     * This method is an internal helper invoked when the catalog may have changed
     * (e.g., after load/save/manage operations).
     * </p>
     */
    private void refreshFiltersAfterCatalogChange() {
        rebuildCategoryCombo(controller.getAllProducts());
        applyFilters();
    }

    /**
     * Refreshes the catalog view using the current controller state.
     * <p>
     * Other windows (such as {@link CatalogManagementWindow}) should call this method
     * to request a safe UI refresh instead of manipulating internal filter logic.
     * </p>
     */
    public void refreshCatalogView() {
        refreshFiltersAfterCatalogChange();
    }

    // -------------------------------------------------------------------------
    // UiSnapshot
    // -------------------------------------------------------------------------

    /**
     * Immutable UI snapshot used to update the view after background operations.
     * <p>
     * Holds:
     * </p>
     * <ul>
     *   <li>{@code success}: whether the requested operation succeeded</li>
     *   <li>{@code items}: the current cart contents</li>
     *   <li>{@code products}: the current product catalog</li>
     * </ul>
     */
    private static final class UiSnapshot {
        private final boolean success;
        private final java.util.List<CartItem> items;
        private final java.util.List<Product> products;

        /**
         * Creates a snapshot representing the UI state after an operation.
         *
         * @param success  whether the operation succeeded
         * @param items    current cart items
         * @param products current catalog products
         */
        private UiSnapshot(boolean success, java.util.List<CartItem> items, java.util.List<Product> products) {
            this.success = success;
            this.items = items;
            this.products = products;
        }
    }
}
