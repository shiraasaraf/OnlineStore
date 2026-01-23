/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.shipping;

import store.order.Order;

import java.util.Objects;

/**
 * Adapter that integrates the external {@link FastShipAPI} with the system.
 *
 * <p>
 * This class implements the Adapter design pattern by adapting the
 * {@link FastShipAPI} interface to the {@link ShippingProvider} interface
 * expected by the application.
 * </p>
 *
 * <ul>
 *   <li><b>Target:</b> {@link ShippingProvider}</li>
 *   <li><b>Adaptee:</b> {@link FastShipAPI}</li>
 *   <li><b>Adapter:</b> {@code Adapter}</li>
 * </ul>
 */
public class Adapter implements ShippingProvider {

    /** Wrapped external shipping API. */
    private final FastShipAPI fastShipAPI;

    /**
     * Creates a new adapter wrapping the given FastShip API instance.
     *
     * @param fastShipAPI external shipping API instance
     * @throws NullPointerException if {@code fastShipAPI} is {@code null}
     */
    public Adapter(FastShipAPI fastShipAPI) {
        this.fastShipAPI = Objects.requireNonNull(fastShipAPI, "fastShipAPI cannot be null");
    }

    /**
     * Ships the given order using the external FastShip API.
     *
     * <p>
     * The order data is translated to the parameters required by
     * {@link FastShipAPI#executeDelivery(int, String, double)}.
     * </p>
     *
     * @param order the order to be shipped
     * @throws IllegalArgumentException if {@code order} is {@code null}
     */
    @Override
    public void shipOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null");
        }

        int orderId = order.getOrderID();
        String recipient = Objects.toString(order.getCustomerUsername(), "UNKNOWN");
        double amount = order.getTotalAmount();

        String tracking = fastShipAPI.executeDelivery(orderId, recipient, amount);

        order.pay();
        order.ship();

        System.out.println("[FastShip] Shipped order " + orderId + ", tracking=" + tracking);
    }
}
