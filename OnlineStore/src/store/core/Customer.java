/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.core;

import store.products.Product;
import store.cart.Cart;
import store.order.Order;
import store.engine.StoreEngine;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a customer in the store system.
 * A customer has a shopping cart and a list of past orders.
 * The customer can add items to the cart, remove them, and complete purchases
 * through the StoreEngine.
 */
public class Customer extends User {

    //data members
    private Cart cart;
    private List<Order> orderHistory;


    /**
     * Constructs a new Customer with an empty cart and an empty order history.
     * Username and email validation is handled by the {@link User} constructor.
     *
     * @param username the customer's username
     * @param email    the customer's email address
     */
    public Customer(String username, String email) {

        super(username, email);

        // initialize an empty cart and empty order history
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    // ------------------------------------------------------------------------------------

    /**
     * Returns the shopping cart associated with this customer.
     *
     * @return the customer's cart
     */
    public Cart getCart() { return cart; }

    //TODO getCartProducts()
//    public List<Product> getCartProducts() {
//        List<Product> products = new ArrayList<>();
//        for (Order order : orderHistory) {
//
//        }
//    }

    /**
     * Returns a copy of the customer's order history.
     * The returned list is a new list instance to preserve encapsulation
     * and prevent external modification of the internal list.
     *
     * @return a copy of the order history list
     */
    public List<Order> getOrderHistory() { return new ArrayList<>(orderHistory); }

    // ------------------------------------------------------------------------------------

    /**
     * Adds a product to the customer's cart.
     * The method checks that the product is not null, the quantity is positive,
     * and there is enough stock. The stock is not updated here — only when an
     * order is actually created.
     *
     * @param p        the product to add
     * @param quantity the amount to add (must be greater than zero)
     * @return true if the product was added, false otherwise
     */
    public boolean addToCart(Product p, int quantity){

        // check if item is existed and its quantity is logical
        if(p == null || quantity <= 0) {
            return false;
        }

        // check if there is enough stock – but do not change it yet
        if(p.getStock() < quantity) {
            return false;
        }

        // delegate to the cart to actually store the product and quantity
        // Assumes Cart has a method: boolean addItem(Product p, int quantity)
        return cart.addItem(p, quantity);
    }


    /**
     * Removes a product completely from the customer's cart.
     *
     * @param p the product to remove
     * @return true if the product was removed from the cart, false otherwise
     */
    public boolean removeFromCart(Product p) {

        if(p == null){
            return false;
        }
        // delegate to cart
        return cart.removeItem(p);
    }


    /**
     * Performs checkout for the customer's cart.
     * If the cart is empty, checkout cannot be completed.
     * Otherwise, the StoreEngine creates an order from the cart.
     * If the order is successfully created, it is added to the order history
     * and the cart is cleared.
     *
     * @return true if the checkout succeeded, false otherwise
     */
    public boolean checkout(){

        // if the cart is empty, there is nothing to checkout
        if(cart.isEmpty()){
            return false;
        }

        //Getting the store engine by calling Singleton
        StoreEngine engine = StoreEngine.getInstance();

        //Creating an order through the store engine
        Order order = engine.createOrderFromCart(cart);

        // If the order failed — return false
        if(order == null) {
            return false;
        }

        //Add to the orders history
        orderHistory.add(order);

        //Clean the cart
        cart.clear();

        return true;
    }

    //-----------------------------------------------------------------------------------

    /**
     * Returns a simple string representation of the customer, including
     * information from the User class and the number of completed orders.
     *
     * @return string representing the customer
     */
    @Override
    public String toString() {
        return "Customer\n" +
                super.toString() + "\n" +
                "Number of orders: " + orderHistory.size();
    }


    //equals Implemented in parent class

}
