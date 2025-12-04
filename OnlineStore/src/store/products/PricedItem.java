/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Represents an item that has a price.
 * Classes implementing this interface must provide access to the item's price
 * and allow updating it according to their validation rules.
 */
public interface PricedItem {

    /**
     * Returns the current price of the item.
     *
     * @return the item's price as a double value
     */
    double getPrice();

    /**
     * Sets a new price for the item.
     * Implementing classes may include validation (e.g., rejecting negative values).
     *
     * @param price the new price to assign
     * @return true if the price was successfully updated, false otherwise
     */
    boolean setPrice(double price);
}
