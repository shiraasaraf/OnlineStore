/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.discount;

/**
 * Strategy interface for applying discounts on a cart subtotal.
 *
 * <p>
 * This is the Strategy design pattern: the store can switch the active
 * discount algorithm at runtime (e.g., no discount, percentage discount,
 * future discounts such as "buy 2 get 1"), without changing cart/order code.
 * </p>
 */
public interface DiscountStrategy {

    /**
     * Applies this discount strategy on the given subtotal.
     *
     * @param subtotal cart subtotal before discount (must be >= 0)
     * @return final total after discount (never negative)
     */
    double apply(double subtotal);

    /**
     * Returns a human-readable name for UI display.
     *
     * @return display name
     */
    String getDisplayName();
}
