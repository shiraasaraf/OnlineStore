package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * A simple management window for the store catalog.
 * Allows a manager to see all products and remove selected ones.
 */
public class CatalogManagementWindow extends JDialog {

    private final StoreController controller;
    private final StoreWindow parentWindow;

    private final DefaultListModel<Product> listModel;
    private final JList<Product> productList;
    private final JButton removeButton;
    private final JButton closeButton;

    public CatalogManagementWindow(StoreWindow parentWindow, StoreController controller) {
        super(parentWindow, "Catalog Management", true); // modal dialog

        this.controller = controller;
        this.parentWindow = parentWindow;

        setSize(500, 400);
        setLocationRelativeTo(parentWindow);

        // List model and JList
        listModel = new DefaultListModel<>();
        productList = new JList<>(listModel);

        // נשתמש ב-toString של Product או נשפר מעט את התצוגה
        productList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Product) {
                    Product p = (Product) value;
                    String text = p.getName() + " | " +
                            p.getCategory() + " | stock: " + p.getStock();
                    label.setText(text);
                }

                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(productList);

        removeButton = new JButton("Delete selected product");
        closeButton = new JButton("Close");

        // Bottom buttons panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(removeButton);
        bottomPanel.add(closeButton);

        setLayout(new BorderLayout(10, 10));
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load products into the list
        refreshProductList();

        // Listeners
        removeButton.addActionListener(this::onRemoveClicked);
        closeButton.addActionListener(e -> dispose());
    }

    /**
     * Loads products from the controller into the list model.
     */
    private void refreshProductList() {
        listModel.clear();
        List<Product> products = controller.getAllProducts();
        for (Product p : products) {
            listModel.addElement(p);
        }
    }

    /**
     * Called when the "Delete selected product" button is clicked.
     */
    private void onRemoveClicked(ActionEvent e) {
        Product selected = productList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No product selected.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product from the catalog?\n" + selected.getName(),
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        boolean removed = controller.removeProduct(selected);

        if (!removed) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to remove product.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }


        // רענון הרשימה בחלון הניהול
        refreshProductList();

        // רענון הקטלוג בחלון הראשי
        parentWindow.setCatalogProducts(controller.getAvailableProducts());
    }
}
