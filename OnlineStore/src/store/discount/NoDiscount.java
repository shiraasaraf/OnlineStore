/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.discount;

/**
 * A discount strategy that applies no discount to the subtotal.
 *
 * <p>
 * This implementation represents the default behavior where the
 * original subtotal remains unchanged.
 * </p>
 */
public final class NoDiscount implements DiscountStrategy {

    /**
     * A shared instance of this discount strategy.
     */
    public static final NoDiscount INSTANCE = new NoDiscount();

    /**
     * Constructs a {@code NoDiscount} strategy.
     */
    public NoDiscount() {
        // no state
    }

    /**
     * Returns the subtotal without applying any discount.
     *
     * @param subtotal the cart subtotal before discount
     * @return the original subtotal, or {@code 0.0} if the subtotal is negative
     */
    @Override
    public double apply(double subtotal) {
        if (subtotal < 0) return 0.0;
        return subtotal;
    }

    /**
     * Returns the display name of this discount strategy.
     *
     * @return the string {@code "No discount"}
     */
    @Override
    public String getDisplayName() {
        return "No discount";
    }
}
