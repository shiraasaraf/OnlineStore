/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.discount;

/**
 * A discount strategy that reduces the subtotal by a fixed percentage.
 */
public final class PercentageDiscount implements DiscountStrategy {

    /** Percentage between 0 and 100. */
    private final double percent;

    /**
     * Creates a percentage discount.
     *
     * @param percent discount percent in range [0,100]
     * @throws IllegalArgumentException if percent is out of range
     */
    public PercentageDiscount(double percent) {
        if (Double.isNaN(percent) || percent < 0.0 || percent > 100.0) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }

    @Override
    public double apply(double subtotal) {
        if (subtotal <= 0) return 0.0;
        double factor = 1.0 - (percent / 100.0);
        double total = subtotal * factor;
        return Math.max(0.0, total);
    }

    @Override
    public String getDisplayName() {
        if (percent == 0.0) return "No discount";
        if (percent == Math.rint(percent)) return ((int) percent) + "% off";
        return percent + "% off";
    }
}
