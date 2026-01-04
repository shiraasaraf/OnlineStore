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
 * Central engine of the store system (Singleton).
 * <p>
 * Holds the shared application state: products, customers, and orders.
 * This class does not perform file I/O; persistence is handled by store.io classes.
 * </p>
 * <p>
 * Thread-safety: the controller synchronizes on the shared engine instance
 * when performing critical operations.
 * </p>
 */
public class StoreEngine {

    /** Singleton instance. */
    private static StoreEngine instance;

    /** All products in the store. */
    private final List<Product> products;

    /** All orders created/loaded in the system. */
    private final List<Order> allOrders;

    /** Registered customers (optional, used for simple username-based separation). */
    private final List<Customer> customers;

    /** Order ID generator (monotonically increases). */
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
     * @return shared store engine instance
     */
    public static StoreEngine getInstance() {
        if (instance == null) {
            instance = new StoreEngine();
        }
        return instance;
    }

    // ---------------------------------------------------------------------
    // Product management
    // ---------------------------------------------------------------------

    /**
     * Adds a product to the catalog.
     * <p>
     * If a product with the same name already exists, stock is increased
     * by the new product's stock amount.
     * </p>
     *
     * @param product product to add
     */
    public void addProduct(Product product) {
        if (product == null) {
            return;
        }

        Product existing = findProductByName(product.getName());
        if (existing != null) {
            int amountToAdd = product.getStock();
            if (amountToAdd > 0) {
                existing.increaseStock(amountToAdd);
            }
        } else {
            products.add(product);
        }
    }

    /**
     * Returns products that are currently in stock.
     *
     * @return list of products with stock &gt; 0
     */
    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();
        for (Product p : products) {
            if (p != null && p.getStock() > 0) {
                available.add(p);
            }
        }
        return available;
    }

    /**
     * Returns a defensive copy of all products in the catalog (including out-of-stock products).
     *
     * @return copy of products list
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Removes a product from the catalog.
     *
     * @param product product to remove
     * @return true if removed; false otherwise
     */
    public boolean removeProduct(Product product) {
        if (product == null) {
            return false;
        }
        return products.remove(product);
    }

    // ---------------------------------------------------------------------
    // Customer management
    // ---------------------------------------------------------------------

    /**
     * Registers a customer (by username uniqueness).
     *
     * @param customer customer to register
     * @return true if registered; false if null or username already exists
     */
    public boolean registerCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }

        for (Customer existing : customers) {
            if (existing != null && existing.getUsername() != null
                    && existing.getUsername().equals(customer.getUsername())) {
                return false;
            }
        }

        customers.add(customer);
        return true;
    }

    /**
     * Finds a registered customer by username (case-insensitive).
     *
     * @param username customer's username
     * @return matching customer or null if not found/invalid input
     */
    public Customer findCustomerByUsername(String username) {
        if (username == null) {
            return null;
        }
        String u = username.trim();
        if (u.isEmpty()) {
            return null;
        }

        for (Customer c : customers) {
            if (c != null && c.getUsername() != null && c.getUsername().equalsIgnoreCase(u)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns a defensive copy of all registered customers.
     *
     * @return copy of customers list
     */
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    // ---------------------------------------------------------------------
    // Order management
    // ---------------------------------------------------------------------

    /**
     * Returns a defensive copy of all orders in the system.
     *
     * @return copy of orders list
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

    /**
     * Creates an order from the given customer's cart.
     * <p>
     * The created order includes the customer's username and the cart is cleared.
     * </p>
     *
     * @param customer customer who performs checkout
     * @return created order, or null if customer/cart invalid or empty
     */
    public Order createOrderFromCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }

        Cart cart = customer.getCart();
        if (cart == null || cart.isEmpty()) {
            return null;
        }

        nextOrderId++;

        Order newOrder = new Order(
                customer.getUsername(),
                nextOrderId,
                cart.getItems(),
                cart.calculateTotal()
        );

        allOrders.add(newOrder);
        cart.clear();

        return newOrder;
    }

    /**
     * Creates an order from a cart without customer identity.
     * Prefer {@link #createOrderFromCustomer(Customer)} when possible.
     *
     * @param cart shopping cart
     * @return created order, or null if cart is invalid or empty
     * @deprecated use {@link #createOrderFromCustomer(Customer)} to keep customer separation
     */
    @Deprecated
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
     * Adds orders loaded from persistent storage into the engine and updates the next order ID.
     *
     * @param orders loaded orders
     */
    public void addLoadedOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (Order o : orders) {
            if (o == null) continue;

            allOrders.add(o);

            if (o.getOrderID() > nextOrderId) {
                nextOrderId = o.getOrderID();
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private Product findProductByName(String name) {
        if (name == null) {
            return null;
        }

        for (Product p : products) {
            if (p != null && p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Looks up a product by name.
     * Used by I/O utilities to resolve products when loading orders.
     *
     * @param name product name
     * @return matching product or null
     */
    public Product findProductPublic(String name) {
        return findProductByName(name);
    }
}
