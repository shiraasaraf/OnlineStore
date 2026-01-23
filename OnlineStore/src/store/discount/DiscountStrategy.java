/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.discount;

/**
 * Defines a strategy for applying a discount to a cart subtotal.
 *
 * <p>
 * Implementations of this interface encapsulate different discount
 * calculation policies that can be applied dynamically.
 * </p>
 */
public interface DiscountStrategy {

    /**
     * Applies the discount to the given subtotal.
     *
     * @param subtotal the cart subtotal before discount (must be non-negative)
     * @return the total amount after applying the discount
     */
    double apply(double subtotal);

    /**
     * Returns a display name describing this discount strategy.
     *
     * @return a human-readable discount name
     */
    String getDisplayName();
}
