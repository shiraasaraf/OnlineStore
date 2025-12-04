/**
 * StoreEngine
 * -----------
 * Main engine class responsible for managing products, customers, and orders
 * within the online store system. Implements a Singleton pattern to ensure
 * there is only one instance of the engine throughout the application.
 *
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.engine;

import java.util.ArrayList;
import java.util.List;

import store.cart.Cart;
import store.products.Product;
import store.order.Order;
import store.core.Customer;

public class StoreEngine {

    /** Singleton instance */
    private static StoreEngine instance = null;

    /** Store data collections */
    private List<Product> products;
    private List<Order> allOrders;
    private List<Customer> customers;

    /** Unique ID generator for orders */
    private static int nextOrderId = 0;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes collections for products, orders, and customers.
     */
    private StoreEngine() {
        this.products = new ArrayList<>();
        this.allOrders = new ArrayList<>();
        this.customers = new ArrayList<>();
    }

    /**
     * Returns the single instance of the StoreEngine.
     * Creates a new instance on first call.
     *
     * @return StoreEngine instance
     */
    public static StoreEngine getInstance() {
        if (instance == null) {
            instance = new StoreEngine();
        }
        return instance;
    }

    // ------------------------------------------------------------------------
    // Product Management
    // ------------------------------------------------------------------------

    /**
     * Adds a product to the store's product list.
     *
     * @param p Product to add
     */
    public void addProduct(Product p) {
        products.add(p);
    }

    /**
     * Returns a list of products that are currently in stock.
     *
     * @return List of available products
     */
    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();

        for (Product p : products) {
            if (p.getStock() > 0) {
                available.add(p);
            }
        }

        return available;   // FIXED: Previously returned "products" by mistake
    }

    // ------------------------------------------------------------------------
    // Customer Management
    // ------------------------------------------------------------------------

    /**
     * Registers a new customer if the username is not already taken.
     *
     * @param c Customer to register
     * @return true if registration succeeded, false if username already exists
     */
    public boolean registerCustomer(Customer c) {
        for (Customer customer : customers) {
            if (c.getUsername().equals(customer.getUsername())) {
                return false;   // Username already taken
            }
        }

        customers.add(c);
        return true;
    }

    // ------------------------------------------------------------------------
    // Order Management
    // ------------------------------------------------------------------------

    /**
     * Creates a new order based on the contents of a given cart.
     * Generates a unique order ID, adds the order to the system,
     * and clears the cart afterward.
     *
     * @param cart Cart to create order from
     * @return the created Order object, or null if the cart is empty
     */
    public Order createOrderFromCart(Cart cart) {

        if (cart == null || cart.isEmpty()) {
            return null;
        }

        nextOrderId++;    // Generate unique order ID

        Order newOrder = new Order(
                nextOrderId,
                cart.getItems(),
                cart.calculateTotal()
        );

        allOrders.add(newOrder);
        cart.clear();     // Empty cart after successful order

        return newOrder;
    }
}
