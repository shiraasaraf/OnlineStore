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

public class StoreController {

    private final StoreEngine engine;
    private final Customer customer;
    private final Manager manager;

    public StoreController(StoreEngine engine, Customer customer, Manager manager) {
        this.engine = engine;
        this.customer = customer;
        this.manager = manager;
    }

    // ---------------------------------------------------------------------
    // Products / Catalog
    // ---------------------------------------------------------------------

    public List<Product> getAvailableProducts() {
        return engine.getAvailableProducts();
    }

    public List<Product> getAllProducts() {
        return engine.getAllProducts();
    }

    public boolean removeProduct(Product product) {
        return engine.removeProduct(product);
    }

    /**
     * Loads products from a CSV file (via ProductCatalogIO) and adds them into the engine.
     * This merges stock if the same product already exists (engine.addProduct handles that).
     */
    public void loadProductsFromFile(File file) throws IOException {
        List<Product> loaded = ProductCatalogIO.loadProductsFromFile(file);
        for (Product p : loaded) {
            engine.addProduct(p);
        }
    }

    /**
     * Saves current products to a CSV file (via ProductCatalogIO).
     */
    public void saveProductsToFile(File file) throws IOException {
        ProductCatalogIO.saveProductsToFile(file, engine.getAllProducts());
    }

    // ---------------------------------------------------------------------
    // Orders
    // ---------------------------------------------------------------------

    public List<Order> getAllOrders() {
        return engine.getAllOrders();
    }

    // ---------------------------------------------------------------------
    // Cart (Customer)
    // ---------------------------------------------------------------------

    public boolean addToCart(Product p, int quantity) {
        return customer.addToCart(p, quantity);
    }

    public List<CartItem> getItems() {
        return customer.getItems();
    }

    // ---------------------------------------------------------------------
    // Permissions
    // ---------------------------------------------------------------------

    public boolean canManage() {
        return manager != null;
    }

}

