/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.shipping;

import store.order.Order;

import java.util.Objects;

/**
 * Adapter class that wraps the external {@link FastShipAPI}.
 *
 * <p>
 * Implements the Adapter Design Pattern:
 * </p>
 * <ul>
 *   <li><b>Target:</b> {@link ShippingProvider}</li>
 *   <li><b>Adaptee:</b> {@link FastShipAPI}</li>
 *   <li><b>Adapter:</b> this class ({@code Adapter})</li>
 * </ul>
 *
 * <p>
 * This allows the system to use FastShip without changing existing code
 * that expects {@link ShippingProvider}.
 * </p>
 */
public class Adapter implements ShippingProvider {

    private final FastShipAPI fastShipAPI;

    /**
     * Constructs an Adapter around FastShip API.
     *
     * @param fastShipAPI external API instance
     */
    public Adapter(FastShipAPI fastShipAPI) {
        this.fastShipAPI = Objects.requireNonNull(fastShipAPI, "fastShipAPI cannot be null");
    }

    @Override
    public void shipOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null");
        }

        // Map our Order -> external API parameters
        int orderId = order.getOrderID();
        String recipient = Objects.toString(order.getCustomerUsername(), "UNKNOWN");
        double amount = order.getTotalAmount();

        // Call the external API (incompatible method name/signature)
        String tracking = fastShipAPI.executeDelivery(orderId, recipient, amount);

        // Optional: advance order status if your model expects it
        // (Only keep these lines if Order has these methods and your flow wants it)
        order.pay();
        order.ship();

        // Tracking is printed (no required storage field mentioned)
        System.out.println("[FastShip] Shipped order " + orderId + ", tracking=" + tracking);
    }
}
