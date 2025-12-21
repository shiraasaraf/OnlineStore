package store.gui.controller;

import java.util.List;
import java.io.File;
import java.io.IOException;

import store.cart.CartItem;
import store.engine.StoreEngine;
import store.core.Customer;
import store.core.Manager;
import store.products.Product;
import store.order.Order;

public class StoreController {

    private final StoreEngine engine;
    private final Customer customer;
    private final Manager manager;

    public StoreController(StoreEngine engine, Customer customer, Manager manager) {
        this.engine = engine;
        this.customer = customer;
        this.manager = manager;
    }

    /**
     * Loads the default catalog file (if exists) using the engine.
     */
    public void loadDefaultCatalogIfExists() {
        engine.loadDefaultCatalogIfExists();
    }

    /**
     * Saves the current catalog to the default catalog file.
     */
    public void saveCatalogToDefaultFile() {
        engine.saveCatalogToDefaultFile();
    }


    public List<Product> getAvailableProducts() {
        return engine.getAvailableProducts();
    }

    /**
     * Requests the engine to load products from the given file.
     */
    public void loadProductsFromFile(File file) throws IOException {
        engine.loadProductsFromFile(file);
    }

    /**
     * Returns all products in the catalog (including out-of-stock).
     */
    public List<Product> getAllProducts() {
        return engine.getAllProducts();
    }

    /**
     * Removes a product from the catalog.
     *
     * @param product the product to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeProduct(Product product) {
        return engine.removeProduct(product);
    }



    /**
     * Requests the engine to save the current product catalog into the given file.
     */
    public void saveProductsToFile(File file) throws IOException {
        engine.saveProductsToFile(file);
    }

    /**
     * Returns a defensive copy of all orders, for use by an OrderHistory window.
     */
    public List<Order> getAllOrders() {
        return engine.getAllOrders();
    }

    public boolean addToCart(Product p, int quantity) {
        return customer.addToCart(p, quantity);
    }

    public List<CartItem> getItems() {
        return customer.getItems();
    }

    public boolean canManage() {
        return manager != null;
    }
}
