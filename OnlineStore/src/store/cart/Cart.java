/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.cart;

import store.products.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shopping cart that holds a collection of {@link CartItem} objects.
 * <p>
 * The cart supports adding and removing products, clearing all items, and
 * calculating the total price. The internal list is encapsulated; callers receive
 * a copy via {@link #getItems()}.
 * </p>
 */
public class Cart {

    /** Internal list of cart items (one per product). */
    private final List<CartItem> items;

    /**
     * Constructs an empty cart.
     */
    public Cart() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds a product to the cart.
     * <ul>
     *   <li>If the product already exists in the cart, its quantity is increased.</li>
     *   <li>Otherwise, a new {@link CartItem} is created and added.</li>
     * </ul>
     *
     * @param product  product to add
     * @param quantity quantity to add (must be &gt; 0)
     * @return true if added/updated; false if input is invalid
     */
    public boolean addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        for (CartItem item : items) {
            if (product.equals(item.getProduct())) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            }
        }

        items.add(new CartItem(product, quantity));
        return true;
    }

    /**
     * Returns a defensive copy of the cart items.
     * <p>
     * Modifying the returned list does not affect the cart.
     * </p>
     *
     * @return copy of current cart items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Removes a product from the cart (removes the entire item, not a single unit).
     *
     * @param product product to remove
     * @return true if removed; false if input is invalid or product not found
     */
    public boolean removeItem(Product product) {
        if (product == null) {
            return false;
        }

        for (int i = 0; i < items.size(); i++) {
            if (product.equals(items.get(i).getProduct())) {
                items.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Calculates the total price of all items in the cart.
     *
     * @return total cost of the cart
     */
    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    /**
     * Removes all items from the cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Checks whether the cart contains no items.
     *
     * @return true if empty; false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns a human-readable representation of the cart contents.
     *
     * @return cart content string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cart\n");

        if (items.isEmpty()) {
            sb.append("Items: (empty)\n");
            return sb.toString();
        }

        sb.append("Items:\n");
        for (CartItem item : items) {
            sb.append(item).append("\n\n");
        }

        return sb.toString();
    }
}
