package store.gui.view;

import store.gui.controller.StoreController;
import store.order.Order;
import store.cart.CartItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * OrderHistoryWindow
 * ------------------
 * A simple window that displays the store's order history in a table.
 * Data comes from the Model through the StoreController (MVC friendly).
 */
public class OrderHistoryWindow extends JDialog {

    private final StoreController controller;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JButton refreshButton;
    private final JButton closeButton;

    public OrderHistoryWindow(JFrame parent, StoreController controller) {
        super(parent, "Order History", true); // modal dialog

        this.controller = controller;

        setSize(800, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // ----- Table -----
        tableModel = new DefaultTableModel(
                new Object[]{"Order ID", "Total Amount", "Items"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ----- Bottom buttons -----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");

        bottom.add(refreshButton);
        bottom.add(closeButton);

        add(bottom, BorderLayout.SOUTH);

        // Listeners
        refreshButton.addActionListener(e -> refreshOrders());
        closeButton.addActionListener(e -> dispose());

        // Initial load
        refreshOrders();
    }

    /**
     * Refresh the table contents from controller.getAllOrders().
     */
    private void refreshOrders() {
        tableModel.setRowCount(0);

        List<Order> orders = controller.getAllOrders();

        if (orders == null || orders.isEmpty()) {
            // optional: show an empty row or just leave table empty
            // Here we leave it empty and show a message once.
            // (If you prefer not to show a popup, tell me and I'll change it.)
            return;
        }

        for (Order o : orders) {
            String itemsText = buildItemsSummary(o);

            tableModel.addRow(new Object[]{
                    o.getOrderID(),
                    String.format("%.2f", o.getTotalAmount()),
                    itemsText
            });
        }
    }

    /**
     * Builds a readable items summary for the order: "Laptop x2; Phone x1"
     */
    private String buildItemsSummary(Order order) {
        if (order == null || order.getItems() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (CartItem item : order.getItems()) {
            if (item == null || item.getProduct() == null) continue;

            sb.append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append("; ");
        }

        // remove last "; "
        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
}
