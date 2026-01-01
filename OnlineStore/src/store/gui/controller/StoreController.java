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
 * </p>
 *
 * <p>
 * MVC note:
 * - The Controller performs the "business flow" of checkout.
 * - The Engine handles only data & logic (no file I/O).
 * - File I/O is delegated to store.io classes.
 * </p>
 *
 * <p>
 * Thread-safety note:
 * - Critical operations on shared state are synchronized on the shared engine instance.
 * - File operations are synchronized inside the IO classes.
 * </p>
 */
public class StoreController {

    /**
     * Lock for product catalog file operations (prevents parallel load/save).
     */
    private static final Object PRODUCT_FILE_LOCK = new Object();

    /** Store engine (model). Shared among all windows. */
    private final StoreEngine engine;

    /** Current customer. */
    private final Customer customer;

    /** Current manager (null when not in manager mode). */
    private final Manager manager;

    /**
     * Constructs a controller for the given users and engine.
     *
     * @param engine   store engine (shared model)
     * @param customer current customer
     * @param manager  current manager (may be null)
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
     * Returns all products in the catalog.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        synchronized (engine) {
            return engine.getAllProducts();
        }
    }

    /**
     * Removes a product from the catalog.
     *
     * @param product product to remove
     * @return true if removed, false otherwise
     */
    public boolean removeProduct(Product product) {
        synchronized (engine) {
            return engine.removeProduct(product);
        }
    }

    /**
     * Loads products from a file and adds them to the engine.
     * File read is synchronized to prevent parallel access to the same file.
     * Adding into the engine is synchronized to keep the shared model consistent.
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
     * Saves current products to a file.
     * Takes a snapshot under engine lock, then writes under file lock.
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
    // Orders
    // ---------------------------------------------------------------------

    /**
     * Returns all orders created in the system.
     *
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        synchronized (engine) {
            return engine.getAllOrders();
        }
    }

    /**
     * Returns the current customer's order history.
     *
     * @return list of customer's orders
     */
    public List<Order> getCustomerOrders() {
        synchronized (engine) {
            return customer.getOrderHistory();
        }
    }

    /**
     * Performs checkout for the current customer.
     * This method is synchronized on the shared engine to keep:
     * stock validation + stock decrease + order creation atomic.
     *
     * <p>
     * MVC-perfect flow:
     * 1) validate stock
     * 2) decrease stock (with rollback)
     * 3) engine creates the order from cart (also clears cart + adds to engine orders)
     * 4) customer adds the order to its personal history
     * 5) save order history using OrderHistoryIO
     * </p>
     *
     * @return true if checkout succeeded, false otherwise
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

            // 1) Validate stock
            for (CartItem item : items) {
                Product p = item.getProduct();
                int qty = item.getQuantity();

                if (p == null || qty <= 0) {
                    return false;
                }
                if (p.getStock() < qty) {
                    return false;
                }
            }

            // 2) Decrease stock (with rollback if anything fails)
            List<CartItem> decreased = new ArrayList<>();
            for (CartItem item : items) {
                Product p = item.getProduct();
                int qty = item.getQuantity();

                boolean ok = p.decreaseStock(qty);
                if (!ok) {
                    // rollback
                    for (CartItem prev : decreased) {
                        Product prevP = prev.getProduct();
                        prevP.increaseStock(prev.getQuantity());
                    }
                    return false;
                }
                decreased.add(item);
            }

            // 3) Create order in engine (engine adds it to allOrders and clears cart)
            Order order = engine.createOrderFromCart(cart);
            if (order == null) {
                // rollback stock if engine failed to create order
                for (CartItem item : decreased) {
                    Product p = item.getProduct();
                    p.increaseStock(item.getQuantity());
                }
                return false;
            }

            // 4) Add to customer's personal history
            boolean added = customer.addOrder(order);
            if (!added) {
                // rollback stock to keep consistency
                for (CartItem item : decreased) {
                    Product p = item.getProduct();
                    p.increaseStock(item.getQuantity());
                }
                return false;
            }

            // 5) Save order to file (IO is centralized and synchronized in OrderHistoryIO)
            OrderHistoryIO.appendOrder(order);

            return true;
        }
    }

    // ---------------------------------------------------------------------
    // Cart (Customer)
    // ---------------------------------------------------------------------

    /**
     * Adds a product to the customer's cart.
     *
     * @param p        product to add
     * @param quantity quantity to add
     * @return true if added, false otherwise
     */
    public boolean addToCart(Product p, int quantity) {
        synchronized (engine) {
            return customer.addToCart(p, quantity);
        }
    }

    /**
     * Returns current cart items.
     *
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        synchronized (engine) {
            return customer.getItems();
        }
    }

    /**
     * Removes a product from the customer's cart.
     *
     * @param p product to remove
     * @return true if removed, false otherwise
     */
    public boolean removeFromCart(Product p) {
        synchronized (engine) {
            return customer.removeFromCart(p);
        }
    }

    // ---------------------------------------------------------------------
    // Permissions (Manager)
    // ---------------------------------------------------------------------

    /**
     * Returns whether the current session has manager permissions.
     *
     * @return true if manager mode is active, false otherwise
     */
    public boolean canManage() {
        return manager != null;
    }

    /**
     * Adds a new product to the catalog (manager only).
     *
     * @param product product to add
     * @return true if added, false otherwise
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
     * @return true if succeeded, false otherwise
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
     * @return true if succeeded, false otherwise
     */
    public boolean decreaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            return product.decreaseStock(amount);
        }
    }
}
