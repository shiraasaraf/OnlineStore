/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.discount;

/**
 * A discount strategy that applies no discount.
 */
public final class NoDiscount implements DiscountStrategy {

    /** Shared singleton instance (optional convenience). */
    public static final NoDiscount INSTANCE = new NoDiscount();

    public NoDiscount() {
        // no state
    }

    @Override
    public double apply(double subtotal) {
        if (subtotal < 0) return 0.0;
        return subtotal;
    }

    @Override
    public String getDisplayName() {
        return "No discount";
    }
}
