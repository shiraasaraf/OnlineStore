package store.gui.controller;
import java.util.List;

import store.cart.CartItem;
import store.engine.*;
import store.core.*;
import store.products.*;


public class StoreController {

    private final StoreEngine engine;
    private final Customer customer;
    private final Manager manager;

    public StoreController(StoreEngine engine, Customer customer, Manager manager) {
        this.engine = engine;
        this.customer = customer;
        this.manager = manager;
    }

    public List<Product> getAvailableProducts() {
        return engine.getAvailableProducts();
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