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
 * <p>
 * A customer owns a {@link Cart} and maintains an order history.
 * This class does not communicate directly with the engine; checkout/order creation
 * is handled by the controller/engine (MVC separation).
 * </p>
 */
public class Customer extends User {

    /** The customer's shopping cart. */
    private final Cart cart;

    /** List of orders placed by the customer (order history). */
    private final List<Order> orderHistory;

    /**
     * Constructs a new customer with an empty cart and an empty order history.
     * Username and email validation are handled by {@link User}.
     *
     * @param username customer's username
     * @param email    customer's email address
     */
    public Customer(String username, String email) {
        super(username, email);
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    /**
     * Returns the customer's cart.
     *
     * @return cart instance
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Returns a defensive copy of the cart items.
     *
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        return cart.getItems();
    }

    /**
     * Returns a defensive copy of the customer's order history.
     *
     * @return copy of order history list
     */
    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }

    /**
     * Adds a product to the cart.
     * <p>
     * This method performs basic validation and checks that total quantity in cart
     * will not exceed current stock.
     * Stock is not decreased here; stock changes happen only during checkout.
     * </p>
     *
     * @param product  product to add
     * @param quantity quantity to add (must be &gt; 0)
     * @return true if added; false otherwise
     */
    public boolean addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        int stock = product.getStock();
        if (stock <= 0) {
            return false;
        }

        // Sum how many units of this product already exist in the cart
        int alreadyInCart = 0;
        for (CartItem item : cart.getItems()) {
            if (item == null) continue;

            Product p = item.getProduct();
            if (p != null && p.equals(product)) {
                alreadyInCart += item.getQuantity();
            }
        }

        // Block if the new total would exceed stock
        if (alreadyInCart + quantity > stock) {
            return false;
        }

        return cart.addItem(product, quantity);
    }

    /**
     * Removes a product completely from the cart.
     *
     * @param product product to remove
     * @return true if removed; false otherwise
     */
    public boolean removeFromCart(Product product) {
        if (product == null) {
            return false;
        }
        return cart.removeItem(product);
    }

    /**
     * Adds an order to the customer's history.
     * Called by the controller after a successful checkout.
     *
     * @param order order to add
     * @return true if added; false if order is null
     */
    public boolean addOrder(Order order) {
        if (order == null) {
            return false;
        }
        orderHistory.add(order);
        return true;
    }

    /**
     * Returns a human-readable representation of the customer, including
     * user details and the number of past orders.
     *
     * @return customer string
     */
    @Override
    public String toString() {
        return "Customer\n" + super.toString() + "\nNumber of orders: " + orderHistory.size();
    }
}
