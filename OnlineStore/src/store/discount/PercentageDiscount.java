/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.discount;

/**
 * A discount strategy that applies a fixed percentage reduction
 * to a cart subtotal.
 */
public final class PercentageDiscount implements DiscountStrategy {

    /**
     * The discount percentage, in the range {@code [0, 100]}.
     */
    private final double percent;

    /**
     * Constructs a percentage-based discount strategy.
     *
     * @param percent the discount percentage (must be between 0 and 100)
     * @throws IllegalArgumentException if {@code percent} is out of range
     */
    public PercentageDiscount(double percent) {
        if (Double.isNaN(percent) || percent < 0.0 || percent > 100.0) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }
        this.percent = percent;
    }

    /**
     * Returns the discount percentage.
     *
     * @return the discount percentage
     */
    public double getPercent() {
        return percent;
    }

    /**
     * Applies the percentage discount to the given subtotal.
     *
     * @param subtotal the cart subtotal before discount
     * @return the subtotal after applying the percentage discount
     */
    @Override
    public double apply(double subtotal) {
        if (subtotal <= 0) return 0.0;
        double factor = 1.0 - (percent / 100.0);
        double total = subtotal * factor;
        return Math.max(0.0, total);
    }

    /**
     * Returns a readable description of this discount strategy.
     *
     * @return a display string representing the discount
     */
    @Override
    public String getDisplayName() {
        if (percent == 0.0) return "No discount";
        if (percent == Math.rint(percent)) return ((int) percent) + "% off";
        return percent + "% off";
    }
}
