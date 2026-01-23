/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.view;

import store.cart.CartItem;
import store.core.SystemUpdatable;
import store.gui.controller.StoreController;
import store.order.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modal dialog that displays order history in a non-editable table.
 *
 * <p>
 * The displayed data depends on the current user's permissions, as determined by the
 * provided {@link StoreController}:
 * </p>
 * <ul>
 *   <li>If {@code controller.canManage()} is {@code true}, all orders are displayed and an
 *       additional "Customer" column is shown.</li>
 *   <li>Otherwise, only the current customer's orders are displayed.</li>
 * </ul>
 */
public class OrderHistoryWindow extends JDialog implements SystemUpdatable {

    /** Controller used to fetch orders. May be {@code null}. */
    private final StoreController controller;

    /** Table model for orders (non-editable). */
    private final DefaultTableModel tableModel;

    /** Orders table. */
    private final JTable table;

    /** Reloads the table data. */
    private final JButton refreshButton;

    /** Closes the dialog. */
    private final JButton closeButton;

    /** Date-time formatter used for displaying order creation time. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Creates a modal "Order History" dialog.
     *
     * <p>
     * If a controller is provided, the dialog registers as an observer of the store engine
     * and refreshes automatically upon model updates.
     * </p>
     *
     * @param parent     the parent frame used for modality and centering
     * @param controller the store controller used to fetch orders (may be {@code null})
     */
    public OrderHistoryWindow(JFrame parent, StoreController controller) {
        super(parent, "Order History", true);

        this.controller = controller;

        if (this.controller != null) {
            this.controller.getEngine().addObserver(this);
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (OrderHistoryWindow.this.controller != null) {
                    OrderHistoryWindow.this.controller.getEngine().removeObserver(OrderHistoryWindow.this);
                }
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });

        setSize(900, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        boolean isManager = (controller != null && controller.canManage());

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
     *
     * <p>
     * If the controller is {@code null}, the method does nothing. The table columns
     * depend on whether the current user has manager permissions.
     * </p>
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
     * Builds a single-line summary of the items in an order.
     *
     * <p>
     * Each item is formatted as {@code "<productName> x<quantity>"} and items are separated by {@code "; "}.
     * Null items or items without a product are skipped.
     * </p>
     *
     * @param order the order whose items should be summarized
     * @return a human-readable summary string, or an empty string if unavailable
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

    /**
     * Formats a {@link LocalDateTime} for display in the table.
     *
     * @param dt the date-time value to format
     * @return the formatted date-time string, or an empty string if {@code dt} is {@code null}
     */
    private String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DATE_TIME_FORMAT);
    }

    /**
     * Receives model-change notifications from the store engine and refreshes the table.
     *
     * <p>
     * The refresh is executed on the Swing Event Dispatch Thread (EDT).
     * </p>
     */
    @Override
    public void update() {
        SwingUtilities.invokeLater(this::refreshOrders);
    }
}
