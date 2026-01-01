/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.io;

import store.cart.CartItem;
import store.order.Order;
import store.products.Product;
import store.engine.StoreEngine;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving order history to/from a CSV file.
 * Centralizes all file I/O for orders and prevents parallel writes.
 */
public class OrderHistoryIO {

    private static final Object ORDER_FILE_LOCK = new Object();

    /** Default CSV file for order history. */
    public static final String ORDER_HISTORY_FILE = "orders_history.csv";

    /**
     * Appends an order to the history file.
     *
     * @param order order to save
     */
    public static void appendOrder(Order order) {
        if (order == null) return;

        synchronized (ORDER_FILE_LOCK) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {

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
     * Uses StoreEngine to resolve products by name (so items will reference real Product objects).
     *
     * @param engine engine used to resolve products by name
     * @return list of loaded orders
     */
    public static List<Order> loadOrders(StoreEngine engine) {
        List<Order> loaded = new ArrayList<>();

        File file = new File(ORDER_HISTORY_FILE);
        if (!file.exists() || !file.isFile()) {
            return loaded;
        }

        synchronized (ORDER_FILE_LOCK) {
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

                    List<CartItem> items = parseItemsSummary(engine, parts[3].trim());
                    Order order = new Order(orderId, items, total, createdAt);
                    loaded.add(order);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return loaded;
    }

    private static List<CartItem> parseItemsSummary(StoreEngine engine, String summary) {
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

            Product p = (engine == null) ? null : engine.findProductPublic(parts[0].trim());
            if (p != null) {
                result.add(new CartItem(p, qty));
            }
        }

        return result;
    }
}
