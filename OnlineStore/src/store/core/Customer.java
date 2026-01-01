/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.core;

import store.cart.Cart;
import store.cart.CartItem;
import store.order.Order;
import store.products.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the store system.
 * A customer has a shopping cart and a list of past orders.
 * The customer can add items to the cart, remove them, and keep order history.
 *
 * <p>
 * Note: The customer does NOT communicate directly with the StoreEngine.
 * Order creation is handled by the Controller/Engine (MVC separation).
 * </p>
 */
public class Customer extends User {

    // Data members
    private final Cart cart;
    private final List<Order> orderHistory;

    /**
     * Constructs a new Customer with an empty cart and an empty order history.
     * Username and email validation is handled by the {@link User} constructor.
     *
     * @param username the customer's username
     * @param email    the customer's email address
     */
    public Customer(String username, String email) {
        super(username, email);
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    // ------------------------------------------------------------------------------------

    /**
     * Returns the shopping cart associated with this customer.
     *
     * @return the customer's cart
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Returns current cart items.
     *
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        return cart.getItems();
    }

    /**
     * Returns a copy of the customer's order history to preserve encapsulation.
     *
     * @return a copy of the order history list
     */
    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }

    // ------------------------------------------------------------------------------------

    /**
     * Adds a product to the customer's cart.
     * The method checks that the product is not null, the quantity is positive,
     * and there is enough stock.
     *
     * <p>
     * Note: Stock is NOT updated here — only when an order is actually created
     * by the controller/engine.
     * </p>
     *
     * @param p        the product to add
     * @param quantity the amount to add (must be greater than zero)
     * @return true if the product was added, false otherwise
     */
    public boolean addToCart(Product p, int quantity) {

        if (p == null || quantity <= 0) {
            return false;
        }

        // check if there is enough stock – but do not change it yet
        if (p.getStock() < quantity) {
            return false;
        }

        return cart.addItem(p, quantity);
    }

    /**
     * Removes a product completely from the customer's cart.
     *
     * @param p the product to remove
     * @return true if the product was removed from the cart, false otherwise
     */
    public boolean removeFromCart(Product p) {
        if (p == null) {
            return false;
        }
        return cart.removeItem(p);
    }

    /**
     * Adds an order to the customer's order history.
     * This is called by the controller after the engine successfully creates the order.
     *
     * @param order the created order
     * @return true if added, false otherwise
     */
    public boolean addOrder(Order order) {
        if (order == null) {
            return false;
        }
        orderHistory.add(order);
        return true;
    }

    // ------------------------------------------------------------------------------------

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

    // equals implemented in parent class
}
