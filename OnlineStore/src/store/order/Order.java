/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import store.cart.CartItem;
import store.core.Persistable;

/**
 * Class representing an order made by a customer.
 * Contains order details such as ID, items, total amount and status.
 */
public class Order implements Persistable {
    private int orderID;
    private List<CartItem> items;
    private double totalAmount;
    private OrderStatus status;

    /**
     * Constructs a new Order instance with given ID, items and total amount.
     * Status is initialized to NEW.
     *
     * @param orderID unique identifier of the order
     * @param items list of CartItem objects in the order (copied internally)
     * @param totalAmount total cost of the order
     */
    public Order(int orderID, List<CartItem> items, double totalAmount) {
        this.orderID = orderID;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.NEW;
    }

    /**
     * Saves the order to a file at the specified path.
     * Implementation is pending.
     *
     * @param path file path to save the order data
     */
    public void saveToFile(String path){
        //TODO
    }

    /**
     * Marks the order as paid.
     *
     * @return true always, indicating the status was set to PAID
     */
    public boolean pay() {
        this.status = OrderStatus.PAID;
        return true;
    }

    /**
     * Marks the order as shipped if it has been paid.
     *
     * @return true if status changed to SHIPPED, false otherwise
     */
    public boolean ship(){
        if(this.status == OrderStatus.PAID){
            this.status = OrderStatus.SHIPPED;
            return true;
        }
        return false;
    }

    /**
     * Marks the order as delivered if it has been shipped.
     *
     * @return true if status changed to DELIVERED, false otherwise
     */
    public boolean deliver(){
        if(this.status == OrderStatus.SHIPPED){
            this.status = OrderStatus.DELIVERED;
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the order,
     * including order ID, status, total amount and item details.
     *
     * @return formatted string describing the order
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderID).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Total Amount: ").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("Items:\n");

        for (CartItem item : items) {
            sb.append(" - ").append(item.toString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Compares this order to another object for equality.
     * Two orders are equal if they have the same orderID.
     *
     * @param obj the object to compare to
     * @return true if obj is an Order with the same orderID, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj instanceof Order) {
            return orderID == ((Order) obj).orderID;
        }
        return false;
    }
}