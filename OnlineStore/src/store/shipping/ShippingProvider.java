/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.shipping;

import store.order.Order;

/**
 * ShippingProvider interface.
 *
 * <p>
 * Target interface for the Adapter Pattern.
 * The store works with this interface instead of depending directly
 * on an external shipping API.
 * </p>
 */
public interface ShippingProvider {

    /**
     * Ships the given order using a shipping service.
     *
     * @param order the order to ship
     * @throws IllegalArgumentException if order is null
     */
    void shipOrder(Order order);
}
