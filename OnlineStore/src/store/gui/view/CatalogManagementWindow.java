/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.gui.controller.StoreController;
import store.products.BookProduct;
import store.products.Category;
import store.products.ClothingProduct;
import store.products.ElectronicsProduct;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Dialog window for catalog / inventory management (Manager).
 * Supports: delete product, increase/decrease stock, add new product.
 * After every successful change, the catalog is saved to a default CSV file.
 */
public class CatalogManagementWindow extends JDialog {

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

    private final JSpinner stockSpinner;

    /**
     * Constructs the catalog management dialog (manager-only).
     *
     * @param parentWindow parent store window
     * @param controller   store controller
     */
    public CatalogManagementWindow(StoreWindow parentWindow, StoreController controller) {
        super(parentWindow, "Catalog / Inventory Management", true);

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

        setSize(750, 470);
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
    }

    private void refreshProductList() {
        listModel.clear();
        List<Product> products = controller.getAllProducts();
        for (Product p : products) {
            listModel.addElement(p);
        }
    }

    private int getStockDelta() {
        Object v = stockSpinner.getValue();
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        return 1;
    }

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

    private void refreshEverywhere() {
        refreshProductList();
        parentWindow.refreshCatalogView();
    }

    /**
     * Saves the current catalog to the default CSV file.
     * Shows an error dialog if saving fails.
     */
    private void saveCatalogToDefaultFile() {
        try {
            controller.saveProductsToFile(DEFAULT_CATALOG_FILE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Catalog was updated, but saving to CSV failed:\n" + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

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
        refreshEverywhere();
    }

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
        refreshEverywhere();
    }

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
        refreshEverywhere();
    }

    private void onAddProduct(ActionEvent e) {
        Product newProduct = showAddProductDialog();
        if (newProduct == null) return;

        boolean ok = controller.addProduct(newProduct);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveCatalogToDefaultFile();
        refreshEverywhere();
    }

    /**
     * Creates a concrete product instance based on the selected Category:
     * BOOKS -> BookProduct, CLOTHING -> ClothingProduct, ELECTRONICS -> ElectronicsProduct.
     *
     * @return created product or null if cancelled/invalid
     */
    /**
     * Creates a concrete product instance based on the selected Category:
     * BOOKS -> BookProduct, CLOTHING -> ClothingProduct, ELECTRONICS -> ElectronicsProduct.
     *
     * @return created product or null if cancelled/invalid
     */
    /**
     * Creates a concrete product instance based on the selected Category:
     * BOOKS -> BookProduct, CLOTHING -> ClothingProduct, ELECTRONICS -> ElectronicsProduct.
     *
     * @return created product or null if cancelled/invalid
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

        // Image path is mandatory
        if (img.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Image path is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Enforce resources/images path format
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

            return new BookProduct(name, price, stock, desc, cat, color, img, author, pages);
        }

        if (cat == Category.CLOTHING) {
            String size = sizeField.getText() == null ? "" : sizeField.getText().trim();
            if (size.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Size is required for clothing.", "Validation", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return new ClothingProduct(name, price, stock, desc, cat, color, img, size);
        }

        // ELECTRONICS
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

        return new ElectronicsProduct(name, price, stock, desc, cat, color, img, warrantyMonths, brand);
    }


}
