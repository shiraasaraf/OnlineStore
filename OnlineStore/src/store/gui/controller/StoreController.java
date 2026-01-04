/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.controller;

import store.cart.Cart;
import store.cart.CartItem;
import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.io.OrderHistoryIO;
import store.io.ProductCatalogIO;
import store.order.Order;
import store.products.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the store GUI.
 * <p>
 * Bridges between the view layer and the model (engine/customer/manager).
 * File I/O is delegated to store.io classes.
 * Thread-safety: critical operations synchronize on the shared engine instance.
 * </p>
 */
public class StoreController {

    /** Lock for product catalog file operations (prevents parallel load/save). */
    private static final Object PRODUCT_FILE_LOCK = new Object();

    /** Shared store engine (model). */
    private final StoreEngine engine;

    /** Current customer (always exists in customer windows). */
    private final Customer customer;

    /** Current manager (non-null only in manager mode). */
    private final Manager manager;

    /**
     * Constructs a controller for the given engine and active user session.
     *
     * @param engine   shared store engine
     * @param customer current customer (required for cart/checkout)
     * @param manager  current manager (null when not in manager mode)
     */
    public StoreController(StoreEngine engine, Customer customer, Manager manager) {
        this.engine = engine;
        this.customer = customer;
        this.manager = manager;
    }

    // ---------------------------------------------------------------------
    // Products / Catalog
    // ---------------------------------------------------------------------

    /**
     * Returns products that are currently in stock.
     *
     * @return list of available products
     */
    public List<Product> getAvailableProducts() {
        synchronized (engine) {
            return engine.getAvailableProducts();
        }
    }

    /**
     * Returns all products in the catalog (including out-of-stock products).
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        synchronized (engine) {
            return engine.getAllProducts();
        }
    }

    /**
     * Removes a product from the catalog (manager usage).
     *
     * @param product product to remove
     * @return true if removed; false otherwise
     */
    public boolean removeProduct(Product product) {
        synchronized (engine) {
            return engine.removeProduct(product);
        }
    }

    /**
     * Loads products from a CSV file and adds them to the engine.
     *
     * @param file source file
     * @throws IOException if reading fails
     */
    public void loadProductsFromFile(File file) throws IOException {
        List<Product> loaded;
        synchronized (PRODUCT_FILE_LOCK) {
            loaded = ProductCatalogIO.loadProductsFromFile(file);
        }

        synchronized (engine) {
            for (Product p : loaded) {
                engine.addProduct(p);
            }
        }
    }

    /**
     * Saves current products to a CSV file.
     *
     * @param file destination file
     * @throws IOException if writing fails
     */
    public void saveProductsToFile(File file) throws IOException {
        List<Product> snapshot;
        synchronized (engine) {
            snapshot = engine.getAllProducts();
        }

        synchronized (PRODUCT_FILE_LOCK) {
            ProductCatalogIO.saveProductsToFile(file, snapshot);
        }
    }

    // ---------------------------------------------------------------------
    // Orders / History
    // ---------------------------------------------------------------------

    /**
     * Returns all orders in the system (manager usage).
     *
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        synchronized (engine) {
            return engine.getAllOrders();
        }
    }

    /**
     * Returns the order history for the current customer only.
     * Orders are filtered by the username stored in the Order.
     *
     * @return list of orders that belong to the current customer
     */
    public List<Order> getCustomerOrders() {
        List<Order> result = new ArrayList<>();

        if (customer == null || customer.getUsername() == null) {
            return result;
        }

        String username = customer.getUsername().trim();
        if (username.isEmpty()) {
            return result;
        }

        synchronized (engine) {
            for (Order o : engine.getAllOrders()) {
                if (o == null) continue;

                String owner = o.getCustomerUsername();
                if (owner != null && owner.equalsIgnoreCase(username)) {
                    result.add(o);
                }
            }
        }

        return result;
    }

    /**
     * Performs checkout for the current customer.
     * <p>
     * Flow:
     * 1) validate stock for all cart items
     * 2) decrease stock (rollback on failure)
     * 3) create order in engine (clears the cart)
     * 4) append order to order-history file
     * </p>
     *
     * @return true if checkout succeeded; false otherwise
     */
    public boolean checkout() {
        if (customer == null) {
            return false;
        }

        synchronized (engine) {
            Cart cart = customer.getCart();
            if (cart == null || cart.isEmpty()) {
                return false;
            }

            List<CartItem> items = cart.getItems();

            // validate
            for (CartItem item : items) {
                if (item == null) return false;

                Product p = item.getProduct();
                int qty = item.getQuantity();

                if (p == null || qty <= 0) return false;
                if (p.getStock() < qty) return false;
            }

            // decrease stock (with rollback)
            List<CartItem> decreased = new ArrayList<>();
            for (CartItem item : items) {
                Product p = item.getProduct();
                int qty = item.getQuantity();

                boolean ok = p.decreaseStock(qty);
                if (!ok) {
                    for (CartItem prev : decreased) {
                        prev.getProduct().increaseStock(prev.getQuantity());
                    }
                    return false;
                }
                decreased.add(item);
            }

            // create order (includes username, clears cart, adds to engine history)
            Order order = engine.createOrderFromCustomer(customer);
            if (order == null) {
                for (CartItem item : decreased) {
                    item.getProduct().increaseStock(item.getQuantity());
                }
                return false;
            }

            // persist
            OrderHistoryIO.appendOrder(order);

            return true;
        }
    }

    // ---------------------------------------------------------------------
    // Cart (Customer)
    // ---------------------------------------------------------------------

    /**
     * Adds a product to the current customer's cart.
     *
     * @param product  product to add
     * @param quantity quantity to add
     * @return true if added; false otherwise
     */
    public boolean addToCart(Product product, int quantity) {
        if (customer == null) return false;
        synchronized (engine) {
            return customer.addToCart(product, quantity);
        }
    }

    /**
     * Returns current customer's cart items.
     *
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        if (customer == null) return new ArrayList<>();
        synchronized (engine) {
            return customer.getItems();
        }
    }

    /**
     * Removes a product from the current customer's cart.
     *
     * @param product product to remove
     * @return true if removed; false otherwise
     */
    public boolean removeFromCart(Product product) {
        if (customer == null) return false;
        synchronized (engine) {
            return customer.removeFromCart(product);
        }
    }

    // ---------------------------------------------------------------------
    // Permissions (Manager)
    // ---------------------------------------------------------------------

    /**
     * Returns whether the current session has manager permissions.
     *
     * @return true if manager mode is active; false otherwise
     */
    public boolean canManage() {
        return manager != null;
    }

    /**
     * Adds a new product to the catalog (manager only).
     *
     * @param product product to add
     * @return true if added; false otherwise
     */
    public boolean addProduct(Product product) {
        if (!canManage() || product == null) return false;

        synchronized (engine) {
            engine.addProduct(product);
            return true;
        }
    }

    /**
     * Increases stock of an existing product (manager only).
     *
     * @param product product to update
     * @param amount  amount to increase (must be > 0)
     * @return true if succeeded; false otherwise
     */
    public boolean increaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            product.increaseStock(amount);
            return true;
        }
    }

    /**
     * Decreases stock of an existing product (manager only).
     *
     * @param product product to update
     * @param amount  amount to decrease (must be > 0)
     * @return true if succeeded; false otherwise
     */
    public boolean decreaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            return product.decreaseStock(amount);
        }
    }
}
