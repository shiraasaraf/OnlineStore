/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.view;

import store.cart.CartItem;
import store.core.SystemUpdatable;
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
 * Main GUI window of the online store.
 *
 * <p>
 * The displayed UI depends on the current session role:
 * </p>
 * <ul>
 *   <li><b>Customer</b>: displays the catalog grid and a right-side panel with
 *       {@link ProductDetailsPanel} and {@link CartPanel}.</li>
 *   <li><b>Manager</b>: displays the catalog grid and management actions (load/save/manage catalog),
 *       without the customer right-side panel.</li>
 * </ul>
 *
 * <p>
 * The window observes model changes via {@link SystemUpdatable}. When the underlying store state
 * changes, {@link #update()} refreshes the relevant UI components on the Swing EDT.
 * </p>
 */
public class StoreWindow extends JFrame implements SystemUpdatable {

    /** Singleton instance for the manager window. */
    private static StoreWindow managerInstance;

    /** Background worker used to run slow tasks off the Swing EDT. */
    private final WindowWorker worker;

    /** Controller used to access store operations and data. */
    private final StoreController controller;

    /** Catalog grid panel (center area). */
    private final JPanel catalogPanel;

    /** Product details panel (customer only; {@code null} for manager sessions). */
    private ProductDetailsPanel detailsPanel;

    /** Shopping cart panel (customer only; {@code null} for manager sessions). */
    private CartPanel cartPanel;

    /** Guards against scheduling multiple full UI refreshes concurrently. */
    private boolean refreshQueued = false;

    private final JButton loadButton = new JButton("Load");
    private final JButton saveButton = new JButton("Save");
    private final JButton manageCatalogButton = new JButton("Manage Catalog");
    private final JButton historyButton = new JButton("Order History");

    private final JTextField searchField = new JTextField(18);
    private final JButton clearSearchButton = new JButton("Clear");
    private final JComboBox<Object> categoryCombo = new JComboBox<>();

    /**
     * Opens the manager store window as a singleton instance.
     *
     * <p>
     * If the manager window is already open, it is brought to the front and reused.
     * </p>
     *
     * @param controller the controller configured for a manager session (must not be {@code null})
     * @throws IllegalArgumentException if {@code controller} is {@code null}
     */
    public static void openManagerWindow(StoreController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }

        if (managerInstance == null || !managerInstance.isDisplayable()) {
            managerInstance = new StoreWindow(controller);
            managerInstance.setTitle("Online Store - Manager");
            managerInstance.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            managerInstance.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    managerInstance = null;
                }
            });
        } else {
            managerInstance.setVisible(true);
            managerInstance.toFront();
            managerInstance.requestFocus();

            managerInstance.setAlwaysOnTop(true);
            managerInstance.setAlwaysOnTop(false);
        }

        managerInstance.setVisible(true);
    }

    /**
     * Constructs the main store window for either a customer or manager session.
     *
     * <p>
     * The window registers as an observer of the store engine and releases resources
     * (observer registration and worker thread) when closed.
     * </p>
     *
     * @param storeController the store controller used by this window (must not be {@code null})
     * @throws IllegalArgumentException if {@code storeController} is {@code null}
     */
    public StoreWindow(StoreController storeController) {
        if (storeController == null) {
            throw new IllegalArgumentException("storeController cannot be null");
        }

        this.controller = storeController;

        String roleName = controller.canManage() ? "Manager" : "Customer";
        this.worker = new WindowWorker(roleName + "-WindowWorker-" + System.identityHashCode(this));

        this.controller.getEngine().addObserver(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                controller.getEngine().removeObserver(StoreWindow.this);
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

        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);

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
            detailsPanel = null;
            cartPanel = null;
        }

        wireActions();

        if (!controller.canManage()) {
            wireCartAndDetails();
        }

        refreshCatalogView();

        if (cartPanel != null) {
            updateCartTotals(controller.getItems());
        }
    }

    /**
     * Builds the search/category filter bar and wires its listeners to refresh the catalog view.
     *
     * @return the filter panel component
     */
    private JPanel buildFiltersPanel() {
        JPanel filtersBar = new JPanel(new FlowLayout(FlowLayout.CENTER));

        filtersBar.add(new JLabel("Search:"));
        filtersBar.add(searchField);
        filtersBar.add(clearSearchButton);

        filtersBar.add(Box.createHorizontalStrut(15));
        filtersBar.add(new JLabel("Category:"));
        filtersBar.add(categoryCombo);

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
     * Builds the role-appropriate actions panel (manager or customer).
     *
     * @return the actions panel component
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

    /**
     * Wires window-level actions such as load/save catalog, opening management tools,
     * and viewing order history.
     *
     * <p>
     * File operations are executed asynchronously via {@link WindowWorker}.
     * </p>
     */
    private void wireActions() {

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

        manageCatalogButton.addActionListener(e -> {
            CatalogManagementWindow.open(this, controller);
            refreshCatalogView();
        });

        historyButton.addActionListener(e -> {
            OrderHistoryWindow dialog = new OrderHistoryWindow(this, controller);
            dialog.setVisible(true);
        });
    }

    /**
     * Updates the cart table contents and totals display for the given items list.
     *
     * @param items the cart items to display
     */
    private void updateCartTotals(List<CartItem> items) {
        if (cartPanel == null) return;

        cartPanel.setItems(items);

        double subtotal = controller.getCartSubtotal();
        double finalTotal = controller.getCartTotalAfterDiscount();
        String discountText = controller.getDiscountDisplayName();

        cartPanel.setTotals(subtotal, discountText, finalTotal);
    }

    /**
     * Wires customer-only interactions between the cart panel and the product details panel.
     *
     * <p>
     * Operations that modify model state are executed asynchronously via {@link WindowWorker}.
     * The catalog view itself is refreshed through observer notifications ({@link #update()}),
     * while this method updates cart/details immediately for responsiveness.
     * </p>
     */
    private void wireCartAndDetails() {
        if (cartPanel == null || detailsPanel == null) return;

        cartPanel.addRemoveItemListener(ev -> {
            JButton btn = (JButton) ev.getSource();
            CartItem item = (CartItem) btn.getClientProperty(CartPanel.PROP_CART_ITEM);
            if (item == null || item.getProduct() == null) return;

            Product p = item.getProduct();

            worker.runAsync(
                    () -> {
                        boolean removed = controller.removeFromCart(p);
                        return new UiSnapshot(removed, controller.getItems());
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

                        updateCartTotals(snapshot.items);
                        detailsPanel.setProduct(detailsPanel.getProduct());
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
                        return new UiSnapshot(ok, controller.getItems());
                    },
                    snapshot -> {
                        updateCartTotals(snapshot.items);
                        detailsPanel.setProduct(detailsPanel.getProduct());

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
                        return new UiSnapshot(added, controller.getItems());
                    },
                    snapshot -> {
                        updateCartTotals(snapshot.items);
                        detailsPanel.setProduct(p);

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

    /**
     * Rebuilds the catalog grid to display the provided list of products.
     *
     * <p>
     * Each product is rendered using {@link ProductPanel}. Clicking a product card updates
     * {@link ProductDetailsPanel} when available (customer session).
     * </p>
     *
     * @param products the products to display in the catalog grid
     */
    public void setCatalogProducts(List<Product> products) {
        catalogPanel.removeAll();

        for (Product p : products) {
            ProductPanel panel = new ProductPanel(p, controller);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (detailsPanel != null) {
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
     * Rebuilds the category combo box according to the categories present in the given product list.
     *
     * @param products the products used to determine available categories
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
     * Applies the current search text and category selection to the catalog list and refreshes the grid.
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
     * Rebuilds available categories and applies the current filters to update the catalog grid.
     */
    private void refreshFiltersAfterCatalogChange() {
        rebuildCategoryCombo(controller.getAllProducts());
        applyFilters();
    }

    /**
     * Refreshes the catalog view based on the latest model state and current filter settings.
     */
    public void refreshCatalogView() {
        refreshFiltersAfterCatalogChange();
    }

    /**
     * Receives model-change notifications from the store engine and refreshes relevant UI components.
     *
     * <p>
     * Multiple rapid update events are coalesced to prevent flooding the EDT with repeated refreshes.
     * </p>
     */
    @Override
    public void update() {
        if (refreshQueued) return;
        refreshQueued = true;

        SwingUtilities.invokeLater(() -> {
            try {
                applyFilters();

                if (cartPanel != null) {
                    updateCartTotals(controller.getItems());
                }
            } finally {
                refreshQueued = false;
            }
        });
    }

    /**
     * Immutable UI snapshot used to update the view after background operations.
     */
    private static final class UiSnapshot {
        private final boolean success;
        private final java.util.List<CartItem> items;

        private UiSnapshot(boolean success, java.util.List<CartItem> items) {
            this.success = success;
            this.items = items;
        }
    }
}
