/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Represents an item whose stock (inventory quantity) can be tracked and modified.
 * Classes implementing this interface must provide access to the current stock
 * and allow increasing or decreasing the amount according to their validation rules.
 */
public interface StockManageable {

    /**
     * Returns the current stock quantity of the item.
     *
     * @return the number of units currently in stock
     */
    int getStock();

    /**
     * Increases the stock by the specified amount.
     * Implementing classes may define validation rules, such as rejecting
     * non-positive values or limiting maximum stock.
     *
     * @param amount the number of units to add to the stock
     * @return true if the stock was successfully increased, false otherwise
     */
    boolean increaseStock(int amount);

    /**
     * Decreases the stock by the specified amount.
     * Implementing classes should ensure that stock cannot become negative,
     * and may reject invalid or oversized reductions.
     *
     * @param amount the number of units to remove from the stock
     * @return true if the stock was successfully decreased, false otherwise
     */
    boolean decreaseStock(int amount);
}
