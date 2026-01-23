package store.gui.controller;

import store.cart.Cart;
import store.cart.CartItem;
import store.core.Customer;
import store.core.Manager;
import store.discount.DiscountStrategy;
import store.discount.NoDiscount;
import store.discount.PercentageDiscount;
import store.engine.StoreEngine;
import store.io.OrderHistoryIO;
import store.io.ProductCatalogIO;
import store.order.Order;
import store.products.Product;
import store.shipping.Adapter;
import store.shipping.FastShipAPI;
import store.shipping.ShippingProvider;
import store.discount.DiscountStrategy;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the store GUI.
 *
 * <p>
 * Acts as an application-layer facade between the UI and the domain model.
 * Handles coordination logic such as catalog loading/saving, cart operations,
 * and checkout workflow.
 * </p>
 *
 * <p>
 * Note: many operations synchronize on the shared {@link StoreEngine} instance
 * to keep the UI actions consistent with the underlying state.
 * </p>
 */
public class StoreController {

    /** Lock for product catalog file operations (prevents parallel load/save). */
    private static final Object PRODUCT_FILE_LOCK = new Object();

    /** Shared store engine (model). */
    private final StoreEngine engine;

    /** Current customer (used for cart/checkout operations). */
    private final Customer customer;

    /** Current manager (non-null only in manager mode). */
    private final Manager manager;

    /** Shipping provider used to dispatch orders. */
    private final ShippingProvider shippingProvider;

    /**
     * Constructs a controller for the given engine and active user session.
     *
     * @param engine   shared store engine (must not be null)
     * @param customer current customer (may be null in manager-only flows)
     * @param manager  current manager (null when not in manager mode)
     */
    public StoreController(StoreEngine engine, Customer customer, Manager manager) {
        if (engine == null) {
            throw new IllegalArgumentException("engine cannot be null");
        }

        this.engine = engine;
        this.customer = customer;
        this.manager = manager;

        this.shippingProvider = new Adapter(new FastShipAPI());
    }

    /**
     * Provides read-only access to the store engine for UI-level features that
     * need aggregated data (e.g., reporting).
     *
     * @return the shared store engine instance
     */
    public StoreEngine getEngine() {
        return engine;
    }

    // ---------------------------------------------------------------------
    // Discount totals (Customer UI)
    // ---------------------------------------------------------------------

    public double getCartSubtotal() {
        if (customer == null) return 0.0;
        synchronized (engine) {
            Cart cart = customer.getCart();
            return (cart == null) ? 0.0 : cart.calculateTotal();
        }
    }

    public double getCartTotalAfterDiscount() {
        if (customer == null) return 0.0;
        synchronized (engine) {
            return engine.calculateTotalAfterDiscount(customer.getCart());
        }
    }

    public String getDiscountDisplayName() {
        synchronized (engine) {
            DiscountStrategy s = engine.getDiscountStrategy();
            return (s == null) ? "No discount" : s.getDisplayName();
        }
    }

    public double getDiscountAmount() {
        double subtotal = getCartSubtotal();
        double total = getCartTotalAfterDiscount();
        return Math.max(0.0, subtotal - total);
    }

    // ---------------------------------------------------------------------
    // Discount switching (Manager)
    // ---------------------------------------------------------------------

    public boolean setNoDiscount() {
        if (!canManage()) return false;
        synchronized (engine) {
            engine.setDiscountStrategy(NoDiscount.INSTANCE);
            return true;
        }
    }

    public boolean setPercentageDiscount(double percent) {
        if (!canManage()) return false;
        synchronized (engine) {
            if (percent <= 0.0) {
                engine.setDiscountStrategy(NoDiscount.INSTANCE);
            } else {
                engine.setDiscountStrategy(new PercentageDiscount(percent));
            }
            return true;
        }
    }

    // ---------------------------------------------------------------------
    // Products / Catalog
    // ---------------------------------------------------------------------

    public List<Product> getAvailableProducts() {
        synchronized (engine) {
            return engine.getAvailableProducts();
        }
    }

    public List<Product> getAllProducts() {
        synchronized (engine) {
            return engine.getAllProducts();
        }
    }

    public boolean removeProduct(Product product) {
        synchronized (engine) {
            return engine.removeProduct(product);
        }
    }

    public void loadProductsFromFile(File file) throws IOException {
        List<Product> loaded;
        synchronized (PRODUCT_FILE_LOCK) {
            loaded = ProductCatalogIO.loadProductsFromFile(file);
        }

        synchronized (engine) {
            engine.addProducts(loaded);
        }
    }

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

    public List<Order> getAllOrders() {
        synchronized (engine) {
            return engine.getAllOrders();
        }
    }

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

            for (CartItem item : items) {
                if (item == null) return false;

                Product p = item.getProduct();
                int qty = item.getQuantity();

                if (p == null || qty <= 0) return false;
                if (p.getStock() < qty) return false;
            }

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

            Order order = engine.createOrderFromCustomer(customer);
            if (order == null) {
                for (CartItem item : decreased) {
                    item.getProduct().increaseStock(item.getQuantity());
                }
                return false;
            }

            try {
                shippingProvider.shipOrder(order);
            } catch (RuntimeException ex) {
                System.err.println("Shipping failed: " + ex.getMessage());
                return false;
            }

            OrderHistoryIO.appendOrder(order);
            return true;
        }
    }

    // ---------------------------------------------------------------------
    // Cart (Customer)
    // ---------------------------------------------------------------------

    public boolean addToCart(Product product, int quantity) {
        if (customer == null) return false;
        synchronized (engine) {
            return customer.addToCart(product, quantity);
        }
    }

    public List<CartItem> getItems() {
        if (customer == null) return new ArrayList<>();
        synchronized (engine) {
            return customer.getItems();
        }
    }

    public boolean removeFromCart(Product product) {
        if (customer == null) return false;
        synchronized (engine) {
            return customer.removeFromCart(product);
        }
    }

    // ---------------------------------------------------------------------
    // Permissions (Manager)
    // ---------------------------------------------------------------------

    public boolean canManage() {
        return manager != null;
    }

    public boolean addProduct(Product product) {
        if (!canManage() || product == null) return false;

        synchronized (engine) {
            engine.addProduct(product);
            return true;
        }
    }

    public boolean increaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            return engine.increaseStock(product, amount);
        }
    }

    public boolean decreaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            return engine.decreaseStock(product, amount);
        }
    }

    public double getPriceAfterDiscount(Product product) {
        if (product == null) return 0.0;
        synchronized (engine) {
            DiscountStrategy s = engine.getDiscountStrategy();
            double base = product.getPrice();
            return (s == null) ? base : s.apply(base);
        }
    }

}