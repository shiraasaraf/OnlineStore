package store.engine;

import store.cart.Cart;
import store.cart.CartItem;
import store.core.Customer;
import store.order.Order;
import store.products.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StoreEngine {

    /** Singleton instance */
    private static StoreEngine instance = null;

    /** Store data collections */
    private final List<Product> products;
    private final List<Order> allOrders;
    private final List<Customer> customers;

    /** Unique ID generator for orders */
    private static int nextOrderId = 0;

    /** Default file used to append order history (CSV). */
    private static final String ORDER_HISTORY_FILE = "orders_history.csv";

    private StoreEngine() {
        this.products = new ArrayList<>();
        this.allOrders = new ArrayList<>();
        this.customers = new ArrayList<>();
    }

    public static StoreEngine getInstance() {
        if (instance == null) {
            instance = new StoreEngine();
        }
        return instance;
    }

    // ------------------------------------------------------------------------
    // Product Management
    // ------------------------------------------------------------------------

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

    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();
        for (Product p : products) {
            if (p.getStock() > 0) {
                available.add(p);
            }
        }
        return available;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public boolean removeProduct(Product product) {
        if (product == null) return false;
        return products.remove(product);
    }

    // ------------------------------------------------------------------------
    // Customer Management
    // ------------------------------------------------------------------------

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

    public List<Order> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

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

    private void appendOrderToHistoryFile(Order order) {
        if (order == null) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {

            StringBuilder itemsSummary = new StringBuilder();
            for (CartItem item : order.getItems()) {
                if (item.getProduct() == null) continue;

                itemsSummary
                        .append(item.getProduct().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(";");
            }

            String line = String.format(
                    "%d,%.2f,%s,%s",
                    order.getOrderID(),
                    order.getTotalAmount(),
                    LocalDateTime.now(),
                    itemsSummary
            );

            writer.write(line);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

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
