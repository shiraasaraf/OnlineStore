/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.view;

import store.core.SystemUpdatable;
import store.gui.controller.StoreController;
import store.products.Category;
import store.products.Product;
import store.products.ProductFactory;

import store.reports.ConsoleWriter;
import store.reports.InventoryReport;
import store.reports.ReportWriter;
import store.reports.SalesReport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Modal manager-only dialog used to manage the product catalog and inventory.
 *
 * <p>
 * This dialog enables catalog administration actions such as adding/removing products,
 * changing stock levels, generating reports, and configuring the active store-wide
 * discount strategy.
 * </p>
 *
 * <p>
 * The dialog registers as an observer of the store model (via {@link SystemUpdatable})
 * and refreshes its product list when the model changes.
 * </p>
 */
public class CatalogManagementWindow extends JDialog implements SystemUpdatable {

    private static CatalogManagementWindow instance;
    private static final File DEFAULT_CATALOG_FILE = new File("products_catalog.csv");

    private final StoreController controller;
    private final StoreWindow parentWindow;

    private final DefaultListModel<Product> listModel;
    private final JList<Product> productList;

    private final JButton removeButton;
    private final JButton increaseStockButton;
    private final JButton decreaseStockButton;
    private final JButton addProductButton;
    private final JButton closeButton;

    private final JButton printReportButton;
    private final JButton saveReportButton;

    private final JSpinner stockSpinner;

    private final JLabel currentDiscountLabel;
    private final JComboBox<String> discountTypeCombo;
    private final JSpinner percentSpinner;
    private final JButton applyDiscountButton;

    /**
     * Opens the catalog management dialog as a singleton modal window.
     *
     * <p>
     * If an existing instance is already open, it is brought to the front and reused.
     * </p>
     *
     * @param parentWindow the owning parent window (must not be {@code null})
     * @param controller   the store controller (must not be {@code null})
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public static void open(StoreWindow parentWindow, StoreController controller) {
        if (parentWindow == null) {
            throw new IllegalArgumentException("parentWindow cannot be null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }

        if (instance == null || !instance.isDisplayable()) {
            instance = new CatalogManagementWindow(parentWindow, controller);

            instance.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    instance = null;
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    instance = null;
                }
            });
        } else {
            instance.setLocationRelativeTo(parentWindow);
            instance.setVisible(true);
            instance.toFront();
            instance.requestFocus();

            instance.setAlwaysOnTop(true);
            instance.setAlwaysOnTop(false);
        }

        instance.setVisible(true);
    }

    /**
     * Constructs a manager-only modal dialog for catalog and inventory management.
     *
     * <p>
     * The dialog registers itself as an observer of the store model and unregisters
     * automatically when closing.
     * </p>
     *
     * @param parentWindow the owning parent window (must not be {@code null})
     * @param controller   the store controller (must not be {@code null})
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws IllegalStateException    if the controller does not have manager permissions
     */
    public CatalogManagementWindow(StoreWindow parentWindow, StoreController controller) {
        super(parentWindow, "Catalog / Inventory Management", true);

        if (parentWindow == null) {
            throw new IllegalArgumentException("parentWindow cannot be null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }

        this.controller = controller;
        this.parentWindow = parentWindow;

        if (!controller.canManage()) {
            JOptionPane.showMessageDialog(
                    parentWindow,
                    "Access denied: manager permissions required.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            throw new IllegalStateException("Manager permissions required");
        }

        this.controller.getEngine().addObserver(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                CatalogManagementWindow.this.controller.getEngine()
                        .removeObserver(CatalogManagementWindow.this);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                CatalogManagementWindow.this.controller.getEngine()
                        .removeObserver(CatalogManagementWindow.this);
            }
        });

        setSize(860, 520);
        setLocationRelativeTo(parentWindow);
        setLayout(new BorderLayout(10, 10));

        listModel = new DefaultListModel<>();
        productList = new JList<>(listModel);
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Product) {
                    Product p = (Product) value;
                    label.setText(
                            p.getName() + " | " + p.getCategory()
                                    + " | price: $" + p.getPrice()
                                    + " | stock: " + p.getStock()
                    );
                }
                return label;
            }
        });
        add(new JScrollPane(productList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rightPanel.add(new JLabel("Store Discount:"));
        rightPanel.add(Box.createVerticalStrut(6));

        currentDiscountLabel = new JLabel("Current: " + controller.getDiscountDisplayName());
        currentDiscountLabel.setFont(currentDiscountLabel.getFont().deriveFont(Font.BOLD));
        rightPanel.add(currentDiscountLabel);

        rightPanel.add(Box.createVerticalStrut(8));

        discountTypeCombo = new JComboBox<>(new String[]{"No discount", "Percentage"});
        discountTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, discountTypeCombo.getPreferredSize().height));
        rightPanel.add(discountTypeCombo);

        rightPanel.add(Box.createVerticalStrut(6));

        JPanel percentRow = new JPanel(new BorderLayout(6, 0));
        percentRow.add(new JLabel("Percent:"), BorderLayout.WEST);

        percentSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
        percentSpinner.setPreferredSize(new Dimension(80, percentSpinner.getPreferredSize().height));
        percentRow.add(percentSpinner, BorderLayout.EAST);

        percentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, percentRow.getPreferredSize().height));
        rightPanel.add(percentRow);

        rightPanel.add(Box.createVerticalStrut(8));

        applyDiscountButton = new JButton("Apply Discount");
        rightPanel.add(applyDiscountButton);

        rightPanel.add(Box.createVerticalStrut(16));
        rightPanel.add(new JSeparator());
        rightPanel.add(Box.createVerticalStrut(12));

        rightPanel.add(new JLabel("Stock change amount:"));
        stockSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
        stockSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, stockSpinner.getPreferredSize().height));
        rightPanel.add(stockSpinner);

        rightPanel.add(Box.createVerticalStrut(10));

        increaseStockButton = new JButton("Increase Stock");
        decreaseStockButton = new JButton("Decrease Stock");
        addProductButton = new JButton("Add New Product");

        rightPanel.add(increaseStockButton);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(decreaseStockButton);

        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(addProductButton);

        rightPanel.add(Box.createVerticalStrut(18));
        rightPanel.add(new JSeparator());
        rightPanel.add(Box.createVerticalStrut(12));

        printReportButton = new JButton("Print Report to Console");
        saveReportButton = new JButton("Save Report to CSV");

        rightPanel.add(printReportButton);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(saveReportButton);

        add(rightPanel, BorderLayout.EAST);

        removeButton = new JButton("Delete selected product");
        closeButton = new JButton("Close");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(removeButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshProductList();

        removeButton.addActionListener(this::onRemoveClicked);
        increaseStockButton.addActionListener(this::onIncreaseStock);
        decreaseStockButton.addActionListener(this::onDecreaseStock);
        addProductButton.addActionListener(this::onAddProduct);
        closeButton.addActionListener(e -> dispose());

        printReportButton.addActionListener(this::onPrintReportToConsole);
        saveReportButton.addActionListener(this::onSaveReportToCsv);

        discountTypeCombo.addActionListener(e -> updateDiscountControlsEnabledState());
        updateDiscountControlsEnabledState();
        applyDiscountButton.addActionListener(this::onApplyDiscount);
    }

    /**
     * Enables or disables discount-related input controls based on the selected discount type.
     */
    private void updateDiscountControlsEnabledState() {
        String type = (String) discountTypeCombo.getSelectedItem();
        boolean percentage = "Percentage".equals(type);
        percentSpinner.setEnabled(percentage);
    }

    /**
     * Extracts the current percentage value from the percentage spinner.
     *
     * @return the spinner value as a {@code double}, or {@code 0.0} if unavailable
     */
    private double getPercentValue() {
        Object v = percentSpinner.getValue();
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0.0;
    }

    /**
     * Applies the selected discount strategy through the controller and updates the UI accordingly.
     *
     * @param e the triggering action event
     */
    private void onApplyDiscount(ActionEvent e) {
        String type = (String) discountTypeCombo.getSelectedItem();
        boolean ok;

        if ("Percentage".equals(type)) {
            double percent = getPercentValue();
            ok = controller.setPercentageDiscount(percent);
        } else {
            ok = controller.setNoDiscount();
        }

        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to apply discount (manager permissions required).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        currentDiscountLabel.setText("Current: " + controller.getDiscountDisplayName());

        JOptionPane.showMessageDialog(
                this,
                "Discount updated successfully.",
                "Discount",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Refreshes the product list model from the current store catalog.
     */
    private void refreshProductList() {
        listModel.clear();
        List<Product> products = controller.getAllProducts();
        for (Product p : products) {
            listModel.addElement(p);
        }
    }

    /**
     * Extracts the current stock delta value from the stock spinner.
     *
     * @return the stock delta as an integer (defaults to {@code 1} if unavailable)
     */
    private int getStockDelta() {
        Object v = stockSpinner.getValue();
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        return 1;
    }

    /**
     * Returns the currently selected product in the list, showing a warning dialog if none is selected.
     *
     * @return the selected product, or {@code null} if no selection exists
     */
    private Product getSelectedOrWarn() {
        Product selected = productList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No product selected.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        return selected;
    }

    /**
     * Saves the current catalog snapshot to the default catalog CSV file using a background worker.
     *
     * <p>
     * The save is performed off the Event Dispatch Thread (EDT) to keep the UI responsive.
     * </p>
     */
    private void saveCatalogToDefaultFile() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.saveProductsToFile(DEFAULT_CATALOG_FILE);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            CatalogManagementWindow.this,
                            "Catalog was updated, but saving to CSV failed:\n" + ex.getMessage(),
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    /**
     * Handles deletion of the currently selected product after user confirmation.
     *
     * @param e the triggering action event
     */
    private void onRemoveClicked(ActionEvent e) {
        Product selected = getSelectedOrWarn();
        if (selected == null) return;

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?\n" + selected.getName(),
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (answer != JOptionPane.YES_OPTION) return;

        boolean removed = controller.removeProduct(selected);
        if (!removed) {
            JOptionPane.showMessageDialog(this, "Failed to remove product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveCatalogToDefaultFile();
    }

    /**
     * Handles increasing stock for the selected product using the amount from the stock spinner.
     *
     * @param e the triggering action event
     */
    private void onIncreaseStock(ActionEvent e) {
        Product selected = getSelectedOrWarn();
        if (selected == null) return;

        int amount = getStockDelta();
        boolean ok = controller.increaseStock(selected, amount);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to increase stock.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveCatalogToDefaultFile();
    }

    /**
     * Handles decreasing stock for the selected product using the amount from the stock spinner.
     *
     * @param e the triggering action event
     */
    private void onDecreaseStock(ActionEvent e) {
        Product selected = getSelectedOrWarn();
        if (selected == null) return;

        int amount = getStockDelta();
        boolean ok = controller.decreaseStock(selected, amount);
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to decrease stock (not enough stock).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        saveCatalogToDefaultFile();
    }

    /**
     * Handles creation of a new product via a modal input dialog and adds it to the catalog.
     *
     * @param e the triggering action event
     */
    private void onAddProduct(ActionEvent e) {
        Product newProduct = showAddProductDialog();
        if (newProduct == null) return;

        boolean ok = controller.addProduct(newProduct);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveCatalogToDefaultFile();
    }

    /**
     * Generates the chosen report type and prints it to the console.
     *
     * @param e the triggering action event
     */
    private void onPrintReportToConsole(ActionEvent e) {
        String chosen = chooseReportType();
        if (chosen == null) return;

        ReportWriter writer = new ConsoleWriter();

        try {
            if ("Inventory".equals(chosen)) {
                InventoryReport report = new InventoryReport(writer);
                report.generate(controller.getEngine());
            } else {
                SalesReport report = new SalesReport(writer);
                report.generate(controller.getEngine());
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Report printed to console.",
                    "Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to generate report:\n" + ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Generates the chosen report type and saves it as a CSV file selected by the user.
     *
     * @param e the triggering action event
     */
    private void onSaveReportToCsv(ActionEvent e) {
        String chosen = chooseReportType();
        if (chosen == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Report (CSV)");
        chooser.setSelectedFile(new File(
                "Inventory".equals(chosen) ? "inventory_report.csv" : "sales_report.csv"
        ));

        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (file == null) return;

        try {
            store.reports.FileWriter writer = new store.reports.FileWriter(file);

            if ("Inventory".equals(chosen)) {
                InventoryReport report = new InventoryReport(writer);
                report.generate(controller.getEngine());
            } else {
                SalesReport report = new SalesReport(writer);
                report.generate(controller.getEngine());
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Report saved to:\n" + file.getAbsolutePath(),
                    "Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save report:\n" + ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Prompts the user to choose which report type to generate.
     *
     * @return {@code "Inventory"} or {@code "Sales"}, or {@code null} if cancelled
     */
    private String chooseReportType() {
        Object[] options = {"Inventory", "Sales"};

        int res = JOptionPane.showOptionDialog(
                this,
                "Choose report type:",
                "Report Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (res < 0 || res >= options.length) {
            return null;
        }
        return (String) options[res];
    }

    /**
     * Receives model update notifications and refreshes the product list and discount label on the EDT.
     */
    @Override
    public void update() {
        SwingUtilities.invokeLater(() -> {
            refreshProductList();
            currentDiscountLabel.setText("Current: " + controller.getDiscountDisplayName());
        });
    }

    /**
     * Shows a dialog for creating a new product, validates user input, and creates the product
     * using {@link ProductFactory} according to the selected {@link Category}.
     *
     * @return the created {@link Product}, or {@code null} if the dialog was cancelled or validation failed
     */
    private Product showAddProductDialog() {
        JTextField nameField = new JTextField(18);
        JTextField priceField = new JTextField(18);
        JTextField stockField = new JTextField(18);
        JComboBox<Category> categoryBox = new JComboBox<>(Category.values());

        JTextField imagePathField = new JTextField(18);

        JTextArea descArea = new JTextArea(4, 18);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JTextField authorField = new JTextField(18);
        JTextField pagesField = new JTextField(18);

        JTextField sizeField = new JTextField(18);

        JTextField warrantyField = new JTextField(18);
        JTextField brandField = new JTextField(18);

        Runnable updateFields = () -> {
            Category c = (Category) categoryBox.getSelectedItem();

            boolean isBook = c == Category.BOOKS;
            boolean isClothing = c == Category.CLOTHING;
            boolean isElec = c == Category.ELECTRONICS;

            authorField.setEnabled(isBook);
            pagesField.setEnabled(isBook);

            sizeField.setEnabled(isClothing);

            warrantyField.setEnabled(isElec);
            brandField.setEnabled(isElec);

            if (!isBook) {
                authorField.setText("");
                pagesField.setText("");
            }
            if (!isClothing) {
                sizeField.setText("");
            }
            if (!isElec) {
                warrantyField.setText("");
                brandField.setText("");
            }
        };

        categoryBox.addActionListener(ev -> updateFields.run());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Name:*"), gc);
        gc.gridx = 1;
        panel.add(nameField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Price (>0):*"), gc);
        gc.gridx = 1;
        panel.add(priceField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Stock (>=0):*"), gc);
        gc.gridx = 1;
        panel.add(stockField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Category:*"), gc);
        gc.gridx = 1;
        panel.add(categoryBox, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Image path (resources/images):*"), gc);
        gc.gridx = 1;
        panel.add(imagePathField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Description:*"), gc);
        gc.gridx = 1;
        panel.add(new JScrollPane(descArea), gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Book: Author:*"), gc);
        gc.gridx = 1;
        panel.add(authorField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Book: Pages (>0):*"), gc);
        gc.gridx = 1;
        panel.add(pagesField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Clothing: Size:*"), gc);
        gc.gridx = 1;
        panel.add(sizeField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Electronics: Warranty Months (>0):*"), gc);
        gc.gridx = 1;
        panel.add(warrantyField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Electronics: Brand:*"), gc);
        gc.gridx = 1;
        panel.add(brandField, gc);

        updateFields.run();

        int res = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Product",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (res != JOptionPane.OK_OPTION) return null;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String priceText = priceField.getText() == null ? "" : priceField.getText().trim();
        String stockText = stockField.getText() == null ? "" : stockField.getText().trim();
        Category cat = (Category) categoryBox.getSelectedItem();
        String img = imagePathField.getText() == null ? "" : imagePathField.getText().trim();
        String desc = descArea.getText() == null ? "" : descArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Category is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (desc.isEmpty() || desc.length() < 3) {
            JOptionPane.showMessageDialog(this, "Description is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (img.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Image path is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (!(img.startsWith("images/") || img.startsWith("images\\"))) {
            JOptionPane.showMessageDialog(
                    this,
                    "Image path must be inside resources/images.\nExample: images/product.jpg",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid stock.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (price <= 0) {
            JOptionPane.showMessageDialog(this, "Price must be > 0.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (stock < 0) {
            JOptionPane.showMessageDialog(this, "Stock must be >= 0.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Color color = Color.BLACK;

        if (cat == Category.BOOKS) {
            String author = authorField.getText() == null ? "" : authorField.getText().trim();
            String pagesText = pagesField.getText() == null ? "" : pagesField.getText().trim();

            if (author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Author is required for books.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            int pages;
            try {
                pages = Integer.parseInt(pagesText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid pages.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (pages <= 0) {
                JOptionPane.showMessageDialog(this, "Pages must be > 0.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return ProductFactory.createBook(name, price, stock, desc, color, img, author, pages);
        }

        if (cat == Category.CLOTHING) {
            String size = sizeField.getText() == null ? "" : sizeField.getText().trim();
            if (size.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Size is required for clothing.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return ProductFactory.createClothing(name, price, stock, desc, color, img, size);
        }

        String warrantyText = warrantyField.getText() == null ? "" : warrantyField.getText().trim();
        String brand = brandField.getText() == null ? "" : brandField.getText().trim();

        if (brand.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brand is required for electronics.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int warrantyMonths;
        try {
            warrantyMonths = Integer.parseInt(warrantyText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid warranty months.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (warrantyMonths <= 0) {
            JOptionPane.showMessageDialog(this, "Warranty months must be > 0.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return ProductFactory.createElectronics(name, price, stock, desc, color, img, warrantyMonths, brand);
    }
}
