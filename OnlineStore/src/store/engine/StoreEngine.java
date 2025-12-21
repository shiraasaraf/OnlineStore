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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;


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
                    order.getCreatedAt(),
                    itemsSummary
            );

            writer.write(line);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * BONUS: load existing orders history from file (if exists)
     */
    public void loadOrderHistoryFromFile() {
        File file = new File(ORDER_HISTORY_FILE);
        if (!file.exists() || !file.isFile()) {
            return; // no history yet – this is fine
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // expected: orderId,totalAmount,date,items...
                String[] parts = line.split(",", 4);
                if (parts.length < 4) {
                    continue;
                }

                int orderId;
                double total;
                try {
                    orderId = Integer.parseInt(parts[0].trim());
                    total = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException ex) {
                    continue;
                }

                String dateStr = parts[2].trim();
                String itemsStr = parts[3].trim();

                LocalDateTime createdAt;
                try {
                    createdAt = LocalDateTime.parse(dateStr);
                } catch (Exception ex) {
                    createdAt = LocalDateTime.now(); // fallback
                }

                // parse items: "Name x2; Other x1; ..."
                List<CartItem> items = parseItemsSummary(itemsStr);

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
     * Helper: parse items summary of format "ProductName x3; Another x1; ..."
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

            String productName = parts[0].trim();
            String qtyStr = parts[1].trim();

            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException ex) {
                continue;
            }

            Product p = findProductByName(productName);
            if (p != null) {
                result.add(new CartItem(p, qty));
            }
        }

        return result;
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
