/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.cart.CartItem;
import store.gui.controller.StoreController;
import store.order.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog window that displays the order history in a table.
 * <p>
 * - Manager can see all orders of all customers.
 * - Customer can see only their own orders.
 * </p>
 */
public class OrderHistoryWindow extends JDialog {

    /** Controller used to fetch orders. */
    private final StoreController controller;

    /** Table model for orders. */
    private final DefaultTableModel tableModel;

    /** Orders table. */
    private final JTable table;

    /** Reloads the table data. */
    private final JButton refreshButton;

    /** Closes the dialog. */
    private final JButton closeButton;

    /**
     * Creates a modal order history dialog.
     *
     * @param parent     parent frame
     * @param controller store controller
     */
    public OrderHistoryWindow(JFrame parent, StoreController controller) {
        super(parent, "Order History", true);

        this.controller = controller;

        setSize(900, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        boolean isManager = (controller != null && controller.canManage());

        // Manager sees an extra column: Customer
        Object[] columns = isManager
                ? new Object[]{"Customer", "Order ID", "Total Amount", "Created At", "Items"}
                : new Object[]{"Order ID", "Total Amount", "Created At", "Items"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");

        bottom.add(refreshButton);
        bottom.add(closeButton);

        add(bottom, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshOrders());
        closeButton.addActionListener(e -> dispose());

        refreshOrders();
    }

    /**
     * Reloads the table contents from the controller.
     * Manager sees all orders; customer sees only their own orders.
     */
    public void refreshOrders() {
        tableModel.setRowCount(0);

        if (controller == null) return;

        boolean isManager = controller.canManage();

        List<Order> orders = isManager
                ? controller.getAllOrders()
                : controller.getCustomerOrders();

        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (Order o : orders) {
            if (o == null) continue;

            if (isManager) {
                tableModel.addRow(new Object[]{
                        o.getCustomerUsername(),
                        o.getOrderID(),
                        String.format("%.2f", o.getTotalAmount()),
                        formatDateTime(o.getCreatedAt()),
                        buildItemsSummary(o)
                });
            } else {
                tableModel.addRow(new Object[]{
                        o.getOrderID(),
                        String.format("%.2f", o.getTotalAmount()),
                        formatDateTime(o.getCreatedAt()),
                        buildItemsSummary(o)
                });
            }
        }
    }

    /**
     * Builds a readable items summary for an order.
     *
     * @param order the order
     * @return items summary string
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

        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    private String formatDateTime(java.time.LocalDateTime dt) {
        if (dt == null) return "";
        java.time.format.DateTimeFormatter f =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dt.format(f);
    }

}
