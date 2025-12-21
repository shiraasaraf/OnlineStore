/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.order;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import store.cart.CartItem;
import store.core.Persistable;

/**
 * Represents a customer's order within the store system.
 *
 * An Order contains:
 *  - A unique order ID
 *  - A list of items (CartItems)
 *  - The total monetary amount
 *  - The current processing status (NEW → PAID → SHIPPED → DELIVERED)
 *
 * This class implements the Persistable interface and is intended
 * to allow saving order data to persistent storage in the future.
 */
public class Order implements Persistable {

    /** Creation date/time of this order */
    private LocalDateTime createdAt;

    /** Unique identifier of the order */
    private int orderID;

    /** List of items included in this order (deep-copied from cart) */
    private List<CartItem> items;

    /** Total monetary value of the order */
    private double totalAmount;

    /** Current status of the order */
    private OrderStatus status;

    /**
     * Constructs a new Order instance.
     * The items list is copied to prevent external modification.
     * The order is initialized with status NEW.
     *
     * @param orderID     the unique identifier assigned to this order
     * @param items       the list of items included in the order
     * @param totalAmount the total calculated price of the order
     */
    public Order(int orderID, List<CartItem> items, double totalAmount) {
        this(orderID, items, totalAmount, LocalDateTime.now());
    }

    public Order(int orderID, List<CartItem> items, double totalAmount, LocalDateTime createdAt) {
        this.orderID = orderID;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.NEW;
        this.createdAt = createdAt;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Saves the order to a file at the specified path.
     * (Implementation expected later.)
     *
     * @param path file system path where the order should be saved
     */
    @Override
    public void saveToFile(String path) {
        // TODO: Implement persistence logic
    }

    // ------------------------------------------------------------------------
    // Order Workflow
    // ------------------------------------------------------------------------

    /**
     * Marks the order as paid.
     *
     * @return true (status always set to PAID)
     */
    public boolean pay() {
        this.status = OrderStatus.PAID;
        return true;
    }

    /**
     * Marks the order as shipped only if the order has already been paid.
     *
     * @return true if status changed to SHIPPED, false otherwise
     */
    public boolean ship() {
        if (this.status == OrderStatus.PAID) {
            this.status = OrderStatus.SHIPPED;
            return true;
        }
        return false;
    }

    /**
     * Marks the order as delivered only if it has already been shipped.
     *
     * @return true if status changed to DELIVERED, false otherwise
     */
    public boolean deliver() {
        if (this.status == OrderStatus.SHIPPED) {
            this.status = OrderStatus.DELIVERED;
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------------
    // Object Methods
    // ------------------------------------------------------------------------

    /**
     * Generates a formatted, human-readable representation of the order.
     *
     * @return a string describing the order ID, status, total amount, and items
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Order ID: ").append(orderID).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Total Amount: ").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("Items:\n");

        for (CartItem item : items) {
            sb.append(" - ").append(item).append("\n");
        }

        return sb.toString();
    }

    /**
     * Determines if this order is equal to another object.
     * Equality is based solely on the orderID.
     *
     * @param obj the object being compared
     * @return true if obj is an Order with the same orderID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof Order) {
            return this.orderID == ((Order) obj).orderID;
        }

        return false;
    }

    // ------------------------------------------------------------------------
    // Getters (Optional — add if needed)
    // ------------------------------------------------------------------------

    public int getOrderID() {
        return orderID;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
