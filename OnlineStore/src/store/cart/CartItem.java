/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.cart;

import store.products.Product;

/**
 * Represents a single item in a shopping cart.
 * <p>
 * A CartItem consists of a product and a positive quantity.
 * Equality is based on the product only (quantity is ignored).
 * </p>
 */
public class CartItem {

    /** The product associated with this cart item. */
    private final Product product;

    /** Quantity of the product (always positive). */
    private int quantity;

    /**
     * Constructs a new cart item.
     *
     * @param product  product to add (must not be null)
     * @param quantity quantity of the product (must be > 0)
     * @throws IllegalArgumentException if input is invalid
     */
    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Returns the product of this cart item.
     *
     * @return product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the quantity of this cart item.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Updates the quantity of this cart item.
     *
     * @param quantity new quantity (must be > 0)
     * @return true if updated; false otherwise
     */
    public boolean setQuantity(int quantity) {
        if (quantity <= 0) {
            return false;
        }
        this.quantity = quantity;
        return true;
    }

    /**
     * Calculates the total price of this cart item.
     *
     * @return product price multiplied by quantity
     */
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    /**
     * Returns a human-readable representation of this cart item.
     *
     * @return string describing product and quantity
     */
    @Override
    public String toString() {
        return product + " x" + quantity;
    }

    /**
     * Two CartItem objects are considered equal if they refer to the same product.
     *
     * @param o object to compare
     * @return true if products are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem other = (CartItem) o;
        return product.equals(other.product);
    }
}
