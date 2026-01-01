/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.engine;

import store.cart.Cart;
import store.core.Customer;
import store.order.Order;
import store.products.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Central engine of the store system.
 * <p>
 * Manages products, customers and orders.
 * Implemented as a Singleton.
 * </p>
 *
 * <p>
 * Note (MVC): This class does NOT handle file I/O. File operations are done in store.io classes.
 * Thread-safety is handled by the Controller by synchronizing on the shared engine instance.
 * </p>
 */
public class StoreEngine {

    /** Singleton instance. */
    private static StoreEngine instance = null;

    /** Store products. */
    private final List<Product> products;

    /** All orders created in the system. */
    private final List<Order> allOrders;

    /** Registered customers. */
    private final List<Customer> customers;

    /** Order ID generator. */
    private static int nextOrderId = 0;

    /**
     * Private constructor (Singleton).
     */
    private StoreEngine() {
        this.products = new ArrayList<>();
        this.allOrders = new ArrayList<>();
        this.customers = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of the store engine.
     *
     * @return store engine instance
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

    public void addProduct(Product p) {
        if (p == null) return;

        Product existing = findProductByName(p.getName());

        if (existing != null) {
            int amountToAdd = p.getStock();
            if (amountToAdd > 0) {
                existing.increaseStock(amountToAdd);
            }
        } else {
            products.add(p);
        }
    }

    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();
        for (Product p : products) {
            if (p.getStock() > 0) {
                available.add(p);
            }
        }
        return available;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public boolean removeProduct(Product product) {
        if (product == null) return false;
        return products.remove(product);
    }

    // ------------------------------------------------------------------------
    // Customer Management
    // ------------------------------------------------------------------------

    public boolean registerCustomer(Customer c) {
        if (c == null) return false;

        for (Customer existing : customers) {
            if (existing.getUsername().equals(c.getUsername())) {
                return false;
            }
        }

        customers.add(c);
        return true;
    }

    // ------------------------------------------------------------------------
    // Order Management
    // ------------------------------------------------------------------------

    public List<Order> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

    /**
     * Creates an order from the given cart.
     * The cart is cleared after successful creation.
     *
     * @param cart shopping cart
     * @return created order, or null if failed
     */
    public Order createOrderFromCart(Cart cart) {
        if (cart == null || cart.isEmpty()) {
            return null;
        }

        nextOrderId++;

        Order newOrder = new Order(
                nextOrderId,
                cart.getItems(),
                cart.calculateTotal()
        );

        allOrders.add(newOrder);
        cart.clear();

        return newOrder;
    }

    /**
     * Adds loaded orders into the engine (used on startup).
     * Also updates the nextOrderId so new orders will get unique IDs.
     *
     * @param orders orders loaded from file
     */
    public void addLoadedOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return;

        for (Order o : orders) {
            if (o == null) continue;
            allOrders.add(o);
            if (o.getOrderID() > nextOrderId) {
                nextOrderId = o.getOrderID();
            }
        }
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

    private Product findProductByName(String name) {
        if (name == null) return null;

        for (Product p : products) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Public product lookup by name.
     * Used by IO utilities to resolve products when loading orders.
     *
     * @param name product name
     * @return matching product or null
     */
    public Product findProductPublic(String name) {
        return findProductByName(name);
    }
}
