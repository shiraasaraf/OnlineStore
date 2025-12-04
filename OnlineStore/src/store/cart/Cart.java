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
 * Represents a shopping cart that holds a collection of CartItem objects.
 * The cart allows adding products, removing products, clearing all items,
 * calculating the total price, and checking whether it is empty.
 */
public class Cart {

    //data member
    private List<CartItem> items;

    /**
     * Constructs a new empty Cart.
     * Initializes the internal list used to store CartItem objects.
     */
    public Cart() {

        this.items = new ArrayList<>(); //Building an empty list as default

    }

    //-------------------------------------------------------------------------------------------

    /**
     * Adds a product to the cart.
     * If the product already exists in the cart, its quantity is increased.
     * If it does not exist, a new CartItem is created and added.
     *
     * @param p        the product to add
     * @param quantity the quantity to add (must be positive)
     * @return true if the item was added or updated, false if the input was invalid
     */
    public boolean addItem(Product p, int quantity){

        //Integrity check
        if(p == null || quantity <= 0) return false;

        // Check if the product is already in the cart
        for (CartItem item : items) {
            if(item.getProduct().equals(p)) {
                int newQuantity = item.getQuantity() + quantity; //if so, increase its quantity
                item.setQuantity(newQuantity);
                return true;
            }
        }

        //if product not found add as a new cart item
        CartItem newItem = new CartItem(p, quantity);
        //add it to the cart
        items.add(newItem);
        return true;
    }


    /**
     * Returns a copy of the list of cart items.
     * This prevents external code from modifying the internal list directly.
     *
     * @return a new List containing all CartItem objects in the cart
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Removes a product from the cart.
     * If the product is found, its corresponding CartItem is removed.
     *
     * @param p the product to remove
     * @return true if the product was removed, false if it was not found or invalid
     */
    public boolean removeItem(Product p){
        if(p == null) return false;

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);

            if (item.getProduct().equals(p)) {
                items.remove(i); //remove by index
                return true;
            }
        }
        return false; //if not found
    }

    /**
     * Calculates the total price of all items in the cart.
     * Each CartItem computes its own total price (price × quantity).
     *
     * @return the total cost of all items in the cart
     */
    public double calculateTotal(){
        double total = 0.0;

        for (CartItem item: items) {
            total += item.getTotalPrice(); //delegation to cartItem
        }

        return total;
    }

    /**
     * Clears all items from the cart, leaving it empty.
     */
    public void clear(){
        items.clear();  //Built Method
    }

    /**
     * Determines whether the cart contains no items.
     *
     * @return true if the cart is empty, false otherwise
     */
    public boolean isEmpty(){
        return items.isEmpty();
    }

    //------------------------------------------------------------------------------------------------

    /**
     * Returns a string representation of this cart.
     * The string contains all cart items and their quantities.
     *
     * @return a string describing the contents of the cart
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cart\n");

        if (items.isEmpty()) {
            sb.append("Items: (empty)\n");
        } else {
            sb.append("Items:\n");
            for (CartItem item : items) {
                sb.append(item).append("\n\n"); // space between products
            }
        }

        return sb.toString();
    }



    //Note: equals is not implemented for Cart. because it's a mutable structure (items change over time)
    //implementing equals for mutable collections may lead to incorrect behavior,
    //and it seems unnecessary, so it is intentionally omitted for now

}
