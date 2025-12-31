/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.engine;

import store.cart.Cart;
import store.cart.CartItem;
import store.core.Customer;
import store.order.Order;
import store.products.Product;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Central engine of the store system.
 * <p>
 * Manages products, customers and orders.
 * Implemented as a Singleton.
 * </p>
 */
public class StoreEngine {

    private static final Object ORDER_FILE_LOCK = new Object();


    /** Singleton instance. */
    private static StoreEngine instance = null;

    /** Store products. */
    private final List<Product> products;

    /** All orders created in the system. */
    private final List<Order> allOrders;

    /** Registered customers. */
    private final List<Customer> customers;

    /** Order ID generator. */
    private static int nextOrderId = 0;

    /** Default CSV file for order history. */
    private static final String ORDER_HISTORY_FILE = "orders_history.csv";

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
     * @return store engine instance
     */
    public static StoreEngine getInstance() {
        if (instance == null) {
            instance = new StoreEngine();
        }
        return instance;
    }

    // ------------------------------------------------------------------------
    // Product Management
    // ------------------------------------------------------------------------

    /**
     * Adds a product to the store.
     * If a product with the same name already exists,
     * its stock is increased.
     *
     * @param p product to add
     */
    public void addProduct(Product p) {
        if (p == null) return;

        Product existing = findProductByName(p.getName());

        if (existing != null) {
            int amountToAdd = p.getStock();
            if (amountToAdd > 0) {
                existing.increaseStock(amountToAdd);
            }
        } else {
            products.add(p);
        }
    }

    /**
     * Returns all products that are currently in stock.
     *
     * @return list of available products
     */
    public synchronized List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();
        for (Product p : products) {
            if (p.getStock() > 0) {
                available.add(p);
            }
        }
        return available;
    }

    /**
     * Returns all products in the store.
     *
     * @return list of all products
     */
    public synchronized List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Removes a product from the store.
     *
     * @param product product to remove
     * @return true if removed, false otherwise
     */
    public boolean removeProduct(Product product) {
        if (product == null) return false;
        return products.remove(product);
    }

    // ------------------------------------------------------------------------
    // Customer Management
    // ------------------------------------------------------------------------

    /**
     * Registers a new customer.
     * Usernames must be unique.
     *
     * @param c customer to register
     * @return true if registration succeeded, false otherwise
     */
    public boolean registerCustomer(Customer c) {
        if (c == null) return false;

        for (Customer existing : customers) {
            if (existing.getUsername().equals(c.getUsername())) {
                return false;
            }
        }

        customers.add(c);
        return true;
    }

    // ------------------------------------------------------------------------
    // Order Management
    // ------------------------------------------------------------------------

    /**
     * Returns all orders created in the system.
     *
     * @return list of all orders
     */
    public synchronized List<Order> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

    /**
     * Creates an order from the given cart.
     * The cart is cleared after successful checkout.
     *
     * @param cart shopping cart
     * @return created order, or null if failed
     */
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

        appendOrderToHistoryFile(newOrder);

        return newOrder;
    }

    /**
     * Appends an order to the history file.
     *
     * @param order order to save
     */
    private void appendOrderToHistoryFile(Order order) {
        if (order == null) return;

        synchronized (ORDER_FILE_LOCK) {
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {

                StringBuilder itemsSummary = new StringBuilder();
                for (CartItem item : order.getItems()) {
                    if (item.getProduct() == null) continue;

                    itemsSummary.append(item.getProduct().getName())
                            .append(" x")
                            .append(item.getQuantity())
                            .append(";");
                }

                String line = String.format(
                        "%d,%.2f,%s,%s",
                        order.getOrderID(),
                        order.getTotalAmount(),
                        order.getCreatedAt(),
                        itemsSummary
                );

                writer.write(line);
                writer.newLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Loads order history from file if it exists.
     */
    public void loadOrderHistoryFromFile() {
        File file = new File(ORDER_HISTORY_FILE);
        if (!file.exists() || !file.isFile()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;

                int orderId;
                double total;
                try {
                    orderId = Integer.parseInt(parts[0].trim());
                    total = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException ex) {
                    continue;
                }

                LocalDateTime createdAt;
                try {
                    createdAt = LocalDateTime.parse(parts[2].trim());
                } catch (Exception ex) {
                    createdAt = LocalDateTime.now();
                }

                List<CartItem> items = parseItemsSummary(parts[3].trim());
                Order order = new Order(orderId, items, total, createdAt);
                allOrders.add(order);

                if (orderId > nextOrderId) {
                    nextOrderId = orderId;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses items summary string into cart items.
     *
     * @param summary items summary string
     * @return list of cart items
     */
    private List<CartItem> parseItemsSummary(String summary) {
        List<CartItem> result = new ArrayList<>();
        if (summary == null || summary.isEmpty()) return result;

        String[] tokens = summary.split(";");
        for (String token : tokens) {
            String t = token.trim();
            if (t.isEmpty()) continue;

            String[] parts = t.split(" x");
            if (parts.length != 2) continue;

            int qty;
            try {
                qty = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException ex) {
                continue;
            }

            Product p = findProductByName(parts[0].trim());
            if (p != null) {
                result.add(new CartItem(p, qty));
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

    /**
     * Finds a product by name (case-insensitive).
     *
     * @param name product name
     * @return matching product or null
     */
    private Product findProductByName(String name) {
        if (name == null) return null;

        for (Product p : products) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
