/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.controller;

import store.cart.CartItem;
import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.io.ProductCatalogIO;
import store.order.Order;
import store.products.Product;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller for the store GUI.
 * <p>
 * Bridges between the view layer and the model (engine/customer/manager).
 * </p>
 */
public class StoreController {

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
        return engine.getAvailableProducts();
    }

    /**
     * Returns all products in the catalog.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return engine.getAllProducts();
    }

    /**
     * Removes a product from the catalog.
     *
     * @param product product to remove
     * @return true if removed, false otherwise
     */
    public boolean removeProduct(Product product) {
        return engine.removeProduct(product);
    }

    /**
     * Loads products from a file and adds them to the engine.
     *
     * @param file source file
     * @throws IOException if reading fails
     */
    public void loadProductsFromFile(File file) throws IOException {
        List<Product> loaded = ProductCatalogIO.loadProductsFromFile(file);
        for (Product p : loaded) {
            engine.addProduct(p);
        }
    }

    /**
     * Saves current products to a file.
     *
     * @param file destination file
     * @throws IOException if writing fails
     */
    public void saveProductsToFile(File file) throws IOException {
        ProductCatalogIO.saveProductsToFile(file, engine.getAllProducts());
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
        return customer.checkout();
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
        return customer.addToCart(p, quantity);
    }

    /**
     * Returns current cart items.
     *
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        return customer.getItems();
    }

    /**
     * Removes a product from the customer's cart.
     *
     * @param p product to remove
     * @return true if removed, false otherwise
     */
    public boolean removeFromCart(Product p) {
        return customer.removeFromCart(p);
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
}
