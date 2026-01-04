/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.order;

import store.cart.CartItem;
import store.core.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a customer's order within the store system.
 *
 * An Order contains:
 *  - A unique order ID
 *  - The username of the customer who placed the order (for order-history separation)
 *  - A list of items (CartItems)
 *  - The total monetary amount
 *  - The current processing status (NEW → PAID → SHIPPED → DELIVERED)
 *
 * This class implements the Persistable interface and is intended
 * to allow saving order data to persistent storage in the future.
 */
public class Order implements Persistable {

    /** If older orders were created without a known customer */
    public static final String UNKNOWN_CUSTOMER = "UNKNOWN";

    /** Creation date/time of this order */
    private final LocalDateTime createdAt;

    /** Unique identifier of the order */
    private final int orderID;

    /** Username of the customer who placed this order */
    private final String customerUsername;

    /** List of items included in this order (deep-copied from cart) */
    private final List<CartItem> items;

    /** Total monetary value of the order */
    private final double totalAmount;

    /** Current status of the order */
    private OrderStatus status;

    // ------------------------------------------------------------------------
    // Constructors (Backward compatible)
    // ------------------------------------------------------------------------

    /**
     * Backward-compatible constructor (existing code can keep using it).
     * Customer username will be set to {@link #UNKNOWN_CUSTOMER}.
     */
    public Order(int orderID, List<CartItem> items, double totalAmount) {
        this(UNKNOWN_CUSTOMER, orderID, items, totalAmount, LocalDateTime.now());
    }

    /**
     * Backward-compatible constructor (existing code can keep using it).
     * Customer username will be set to {@link #UNKNOWN_CUSTOMER}.
     */
    public Order(int orderID, List<CartItem> items, double totalAmount, LocalDateTime createdAt) {
        this(UNKNOWN_CUSTOMER, orderID, items, totalAmount, createdAt);
    }

    /**
     * New recommended constructor: includes the customer username.
     */
    public Order(String customerUsername, int orderID, List<CartItem> items, double totalAmount) {
        this(customerUsername, orderID, items, totalAmount, LocalDateTime.now());
    }

    /**
     * Full constructor: includes the customer username + createdAt (useful for loading history from file).
     */
    public Order(String customerUsername, int orderID, List<CartItem> items, double totalAmount, LocalDateTime createdAt) {
        if (items == null) {
            throw new IllegalArgumentException("items list cannot be null");
        }
        this.orderID = orderID;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.NEW;

        LocalDateTime t = (createdAt == null) ? LocalDateTime.now() : createdAt;
        this.createdAt = t.withNano(0);


        // Normalize username: never keep null/blank
        String normalized = Objects.toString(customerUsername, "").trim();
        this.customerUsername = normalized.isEmpty() ? UNKNOWN_CUSTOMER : normalized;
    }

    // ------------------------------------------------------------------------
    // Persistable
    // ------------------------------------------------------------------------

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
     * @return a string describing the order ID, customer, status, total amount, and items
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Order ID: ").append(orderID).append("\n");

        // Show customer line (helps admin history screen)
        sb.append("Customer: ").append(customerUsername).append("\n");

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

    @Override
    public int hashCode() {
        return Integer.hashCode(orderID);
    }

    // ------------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------------

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getOrderID() {
        return orderID;
    }

    /**
     * Username of the customer who placed the order.
     * For older orders created before this feature, it may be {@link #UNKNOWN_CUSTOMER}.
     */
    public String getCustomerUsername() {
        return customerUsername;
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
