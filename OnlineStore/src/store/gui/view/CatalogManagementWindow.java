/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialog window for catalog management.
 * <p>
 * Displays all products and allows removing a selected product.
 * </p>
 */
public class CatalogManagementWindow extends JDialog {

    /** Controller used to access catalog operations. */
    private final StoreController controller;

    /** Parent store window to refresh catalog after changes. */
    private final StoreWindow parentWindow;

    /** List model backing the products list. */
    private final DefaultListModel<Product> listModel;

    /** Products list UI component. */
    private final JList<Product> productList;

    /** Removes the selected product. */
    private final JButton removeButton;

    /** Closes the dialog. */
    private final JButton closeButton;

    /**
     * Creates a modal catalog management dialog.
     *
     * @param parentWindow parent window
     * @param controller   store controller
     */
    public CatalogManagementWindow(StoreWindow parentWindow, StoreController controller) {
        super(parentWindow, "Catalog Management", true);

        this.controller = controller;
        this.parentWindow = parentWindow;

        setSize(500, 400);
        setLocationRelativeTo(parentWindow);

        listModel = new DefaultListModel<>();
        productList = new JList<>(listModel);

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
                    label.setText(p.getName() + " | " + p.getCategory() + " | stock: " + p.getStock());
                }

                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(productList);

        removeButton = new JButton("Delete selected product");
        closeButton = new JButton("Close");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(removeButton);
        bottomPanel.add(closeButton);

        setLayout(new BorderLayout(10, 10));
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshProductList();

        removeButton.addActionListener(this::onRemoveClicked);
        closeButton.addActionListener(e -> dispose());
    }

    /**
     * Reloads products from the controller into the list model.
     */
    private void refreshProductList() {
        listModel.clear();
        List<Product> products = controller.getAllProducts();
        for (Product p : products) {
            listModel.addElement(p);
        }
    }

    /**
     * Handles remove button click.
     *
     * @param e action event
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

        refreshProductList();
        parentWindow.setCatalogProducts(controller.getAvailableProducts());
    }
}
