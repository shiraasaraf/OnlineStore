/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.cart;

import store.products.Product;

/**
 * Represents a single item in the shopping cart.
 * A CartItem contains a product and the quantity chosen by the customer.
 */
public class CartItem {

    //data members
    private Product product;
    private int quantity;


    /**
     * Creates a new CartItem with the given product and quantity.
     * Quantity must be positive.
     *
     * @param product  the product added to the cart
     * @param quantity the quantity of the product (must be > 0)
     */
    public CartItem (Product product, int quantity) {

        this.product = product;

        this.quantity = 1;  // default quantity if invalid

        setQuantity(quantity); // will change only if quantity > 0
    }

    //------------------------------------------------------------------------------------

    /**
     * Sets the quantity of this cart item.
     * The quantity is updated only if the given value is greater than zero.
     *
     * @param q the new quantity
     * @return true if the quantity was updated, false otherwise
     */
    public boolean setQuantity(int q){
        if (q > 0) {
            this.quantity = q;
            return true;
        }
        return false;
    }


    /**
     * Returns the product of this cart item.
     *
     * @return the product
     */
    public Product getProduct() { return  product; }

    /**
     * Returns the quantity of this cart item.
     *
     * @return the quantity
     */
    public int getQuantity() { return quantity;}


    /**
     * Calculates the total price for this cart item:
     * product price multiplied by quantity.
     *
     * @return the total price of this cart item
     */
    public double getTotalPrice(){
        return product.getPrice() * quantity;
    }


    //------------------------------------------------------------------------------------

    /**
     * Returns a string representation of this cart item,
     * including the product and its quantity.
     *
     * @return a string describing this cart item
     */
    @Override
    public String toString() {
        return "Cart item\n" +
                "Product: " + getProduct() + "\n" +
                "Quantity: " + getQuantity();
    }


    /**
     * Compares this CartItem to another object.
     * Two CartItem objects are considered equal if their products are equal.
     * The quantity is not taken into account.
     *
     * @param o the object to compare with
     * @return {@code true} if both CartItems refer to the same product,
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {

        if(this == o) return true;

        if(!(o instanceof CartItem)) return false;

        CartItem other = (CartItem) o;

        return this.product.equals(other.product);

    }

}
