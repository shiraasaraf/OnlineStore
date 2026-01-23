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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates interactions between the GUI layer and the store domain model.
 *
 * <p>
 * This controller mediates user actions (e.g., managing discounts, loading/saving
 * the product catalog, cart operations, and checkout) and delegates business
 * logic to the underlying {@link StoreEngine} and related model objects.
 * </p>
 *
 * <p>
 * Thread-safety: GUI actions may be triggered from different event contexts.
 * This class synchronizes on the shared {@link StoreEngine} instance for
 * consistency of operations that read/modify shared store state. File operations
 * on the product catalog are additionally guarded by an internal lock to prevent
 * concurrent load/save conflicts.
 * </p>
 */
public class StoreController {

    /** Lock object used to serialize product catalog file operations. */
    private static final Object PRODUCT_FILE_LOCK = new Object();

    /** The shared store engine backing the application state. */
    private final StoreEngine engine;

    /** The active customer for cart and checkout operations (may be {@code null}). */
    private final Customer customer;

    /** The active manager for administrative actions (may be {@code null}). */
    private final Manager manager;

    /** The shipping provider used to dispatch orders during checkout. */
    private final ShippingProvider shippingProvider;

    /**
     * Creates a new controller bound to the given store engine and active session users.
     *
     * @param engine   the shared store engine instance (must not be {@code null})
     * @param customer the active customer (may be {@code null} for manager-only flows)
     * @param manager  the active manager (may be {@code null} when not in manager mode)
     * @throws IllegalArgumentException if {@code engine} is {@code null}
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
     * Returns the shared store engine instance.
     *
     * @return the store engine used by this controller
     */
    public StoreEngine getEngine() {
        return engine;
    }

    // ---------------------------------------------------------------------
    // Discount totals (Customer UI)
    // ---------------------------------------------------------------------

    /**
     * Computes the current cart subtotal for the active customer.
     *
     * @return the cart subtotal, or {@code 0.0} if there is no active customer/cart
     */
    public double getCartSubtotal() {
        if (customer == null) return 0.0;
        synchronized (engine) {
            Cart cart = customer.getCart();
            return (cart == null) ? 0.0 : cart.calculateTotal();
        }
    }

    /**
     * Computes the cart total after applying the currently active discount strategy.
     *
     * @return the discounted total, or {@code 0.0} if there is no active customer
     */
    public double getCartTotalAfterDiscount() {
        if (customer == null) return 0.0;
        synchronized (engine) {
            return engine.calculateTotalAfterDiscount(customer.getCart());
        }
    }

    /**
     * Returns the display name of the currently active discount strategy.
     *
     * @return a human-readable discount name (defaults to {@code "No discount"} when none is set)
     */
    public String getDiscountDisplayName() {
        synchronized (engine) {
            DiscountStrategy s = engine.getDiscountStrategy();
            return (s == null) ? "No discount" : s.getDisplayName();
        }
    }

    /**
     * Computes the absolute discount amount for the active customer's cart.
     *
     * @return the difference between subtotal and discounted total (never negative)
     */
    public double getDiscountAmount() {
        double subtotal = getCartSubtotal();
        double total = getCartTotalAfterDiscount();
        return Math.max(0.0, subtotal - total);
    }

    // ---------------------------------------------------------------------
    // Discount switching (Manager)
    // ---------------------------------------------------------------------

    /**
     * Sets the store discount strategy to "no discount".
     *
     * @return {@code true} if the operation was permitted and applied; {@code false} otherwise
     */
    public boolean setNoDiscount() {
        if (!canManage()) return false;
        synchronized (engine) {
            engine.setDiscountStrategy(NoDiscount.INSTANCE);
            return true;
        }
    }

    /**
     * Sets a percentage-based discount strategy.
     *
     * <p>
     * If {@code percent <= 0.0}, the strategy is set to {@link NoDiscount}.
     * Otherwise, a new {@link PercentageDiscount} is configured.
     * </p>
     *
     * @param percent the discount percentage
     * @return {@code true} if the operation was permitted and applied; {@code false} otherwise
     * @throws IllegalArgumentException if {@code percent} is invalid for {@link PercentageDiscount}
     */
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

    /**
     * Returns products that are currently available for purchase.
     *
     * @return a list of available products as provided by the model
     */
    public List<Product> getAvailableProducts() {
        synchronized (engine) {
            return engine.getAvailableProducts();
        }
    }

    /**
     * Returns all products known to the store (including those not currently available).
     *
     * @return a list of all products as provided by the model
     */
    public List<Product> getAllProducts() {
        synchronized (engine) {
            return engine.getAllProducts();
        }
    }

    /**
     * Removes the given product from the store catalog.
     *
     * @param product the product to remove
     * @return {@code true} if the product was removed; {@code false} otherwise
     */
    public boolean removeProduct(Product product) {
        synchronized (engine) {
            return engine.removeProduct(product);
        }
    }

    /**
     * Loads products from the given file and adds them into the store catalog.
     *
     * @param file the source file to load from
     * @throws IOException if reading/parsing the file fails
     */
    public void loadProductsFromFile(File file) throws IOException {
        List<Product> loaded;
        synchronized (PRODUCT_FILE_LOCK) {
            loaded = ProductCatalogIO.loadProductsFromFile(file);
        }

        synchronized (engine) {
            engine.addProducts(loaded);
        }
    }

    /**
     * Saves the current store catalog into the given file.
     *
     * @param file the destination file to save into
     * @throws IOException if writing to the file fails
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
     * Returns all orders stored in the system.
     *
     * @return a list of all orders as provided by the model
     */
    public List<Order> getAllOrders() {
        synchronized (engine) {
            return engine.getAllOrders();
        }
    }

    /**
     * Returns the order history for the active customer.
     *
     * <p>
     * Orders are filtered by matching {@link Order#getCustomerUsername()} to the
     * active customer's username (case-insensitive).
     * </p>
     *
     * @return a list of orders belonging to the active customer; empty if not applicable
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
     * Performs checkout for the active customer's cart.
     *
     * <p>
     * This method validates the cart contents, updates inventory quantities,
     * creates an order in the model, triggers shipping, and appends the order to
     * persistent history storage.
     * </p>
     *
     * @return {@code true} if checkout completed successfully; {@code false} otherwise
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

    /**
     * Adds a product to the active customer's cart.
     *
     * @param product  the product to add
     * @param quantity the quantity to add
     * @return {@code true} if the item was added; {@code false} otherwise
     */
    public boolean addToCart(Product product, int quantity) {
        if (customer == null) return false;
        synchronized (engine) {
            return customer.addToCart(product, quantity);
        }
    }

    /**
     * Returns the cart items for the active customer.
     *
     * @return a list of cart items; empty if there is no active customer
     */
    public List<CartItem> getItems() {
        if (customer == null) return new ArrayList<>();
        synchronized (engine) {
            return customer.getItems();
        }
    }

    /**
     * Removes a product from the active customer's cart.
     *
     * @param product the product to remove
     * @return {@code true} if the product was removed; {@code false} otherwise
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
     * Indicates whether the current session has manager permissions.
     *
     * @return {@code true} if a manager is present; {@code false} otherwise
     */
    public boolean canManage() {
        return manager != null;
    }

    /**
     * Adds a new product to the catalog (manager-only operation).
     *
     * @param product the product to add
     * @return {@code true} if the product was added; {@code false} otherwise
     */
    public boolean addProduct(Product product) {
        if (!canManage() || product == null) return false;

        synchronized (engine) {
            engine.addProduct(product);
            return true;
        }
    }

    /**
     * Increases stock for a product (manager-only operation).
     *
     * @param product the product to update
     * @param amount  the amount to increase by (must be positive)
     * @return {@code true} if stock was updated; {@code false} otherwise
     */
    public boolean increaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            return engine.increaseStock(product, amount);
        }
    }

    /**
     * Decreases stock for a product (manager-only operation).
     *
     * @param product the product to update
     * @param amount  the amount to decrease by (must be positive)
     * @return {@code true} if stock was updated; {@code false} otherwise
     */
    public boolean decreaseStock(Product product, int amount) {
        if (!canManage() || product == null || amount <= 0) return false;

        synchronized (engine) {
            return engine.decreaseStock(product, amount);
        }
    }

    /**
     * Computes a product price after applying the currently active discount strategy.
     *
     * @param product the product whose price should be calculated
     * @return the discounted price, or {@code 0.0} if {@code product} is {@code null}
     */
    public double getPriceAfterDiscount(Product product) {
        if (product == null) return 0.0;
        synchronized (engine) {
            DiscountStrategy s = engine.getDiscountStrategy();
            double base = product.getPrice();
            return (s == null) ? base : s.apply(base);
        }
    }

}
