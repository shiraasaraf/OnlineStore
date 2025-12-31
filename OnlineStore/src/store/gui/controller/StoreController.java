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
 */
public class StoreController {

    private static final Object ENGINE_LOCK = new Object();
    private static final Object PRODUCT_FILE_LOCK = new Object();

    /** Store engine (model). */
    private final StoreEngine engine;

    /** Current customer. */
    private final Customer customer;

    /** Current manager (null when not in manager mode). */
    private final Manager manager;

    /**
     * Constructs a controller for the given users and engine.
     *
     * @param engine   store engine
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
        synchronized (ENGINE_LOCK) {
            return engine.getAvailableProducts();
        }
    }


    /**
     * Returns all products in the catalog.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        synchronized (ENGINE_LOCK) {
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
        synchronized (ENGINE_LOCK) {
            return engine.removeProduct(product);
        }
    }


    /**
     * Loads products from a file and adds them to the engine.
     *
     * @param file source file
     * @throws IOException if reading fails
     */
    public void loadProductsFromFile(File file) throws IOException {
        List<Product> loaded;

        synchronized (PRODUCT_FILE_LOCK) {
            loaded = ProductCatalogIO.loadProductsFromFile(file);
        }

        synchronized (ENGINE_LOCK) {
            for (Product p : loaded) {
                engine.addProduct(p);
            }
        }
    }



    /**
     * Saves current products to a file.
     *
     * @param file destination file
     * @throws IOException if writing fails
     */
    public void saveProductsToFile(File file) throws IOException {
        List<Product> snapshot;

        synchronized (ENGINE_LOCK) {
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
        return engine.getAllOrders();
    }

    /**
     * Performs checkout for the current customer.
     *
     * @return true if checkout succeeded, false otherwise
     */
    public boolean checkout() {
        if (customer == null) {
            return false;
        }

        synchronized (ENGINE_LOCK) {
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

            // 2) Decrease stock
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

            // 3) Create order (existing flow)
            boolean ok = customer.checkout();

            if (!ok) {
                // rollback if checkout failed
                for (CartItem item : decreased) {
                    Product p = item.getProduct();
                    p.increaseStock(item.getQuantity());
                }
            }

            return ok;
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
        synchronized (ENGINE_LOCK) {
            return customer.addToCart(p, quantity);
        }
    }


    /**
     * Returns current cart items.
     *
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        synchronized (ENGINE_LOCK) {
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
        synchronized (ENGINE_LOCK) {
            return customer.removeFromCart(p);
        }
    }

    // ---------------------------------------------------------------------
    // Permissions
    // ---------------------------------------------------------------------

    /**
     * Returns whether the current session has manager permissions.
     *
     * @return true if manager mode is active, false otherwise
     */
    public boolean canManage() {
        return manager != null;
    }

    public boolean addProduct(Product product) {
        if (!canManage() || product == null) return false;
        synchronized (ENGINE_LOCK) {
            engine.addProduct(product);
            return true;
        }
    }

    public boolean increaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;
        synchronized (ENGINE_LOCK) {
            product.increaseStock(amount);
            return true;
        }
    }

    public boolean decreaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;
        synchronized (ENGINE_LOCK) {
            return product.decreaseStock(amount);
        }
    }

}
