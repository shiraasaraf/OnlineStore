/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.io;

import store.cart.CartItem;
import store.engine.StoreEngine;
import store.order.Order;
import store.products.Product;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving order history to/from a CSV file.
 * Centralizes all file I/O for orders and prevents parallel writes.
 *
 * CSV formats supported:
 *
 * 1) NEW (recommended):
 *    username,orderId,total,createdAt,itemsSummary
 *
 * 2) OLD (backward compatible):
 *    orderId,total,createdAt,itemsSummary
 */
public class OrderHistoryIO {

    private static final Object ORDER_FILE_LOCK = new Object();

    /** Default CSV file for order history. */
    public static final String ORDER_HISTORY_FILE = "orders_history.csv";

    /**
     * Appends an order to the history file.
     *
     * New format:
     *   username,orderId,total,createdAt,itemsSummary
     *
     * @param order order to save
     */
    public static void appendOrder(Order order) {
        if (order == null) return;

        synchronized (ORDER_FILE_LOCK) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {

                String itemsSummary = buildItemsSummary(order);

                // IMPORTANT: username first (so admin/history can filter + show owner)
                String line = String.format(
                        "%s,%d,%.2f,%s,%s",
                        safeCsv(order.getCustomerUsername()),
                        order.getOrderID(),
                        order.getTotalAmount(),
                        order.getCreatedAt(),
                        safeCsv(itemsSummary)
                );

                writer.write(line);
                writer.newLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String buildItemsSummary(Order order) {
        StringBuilder itemsSummary = new StringBuilder();
        for (CartItem item : order.getItems()) {
            if (item == null || item.getProduct() == null) continue;

            // Keep it simple: ProductName xQTY;
            itemsSummary.append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(";");
        }
        return itemsSummary.toString();
    }

    /**
     * Loads order history from file if it exists.
     * Uses StoreEngine to resolve products by name (so items will reference real Product objects).
     *
     * Supports BOTH:
     *  - NEW format: username,orderId,total,createdAt,itemsSummary
     *  - OLD format: orderId,total,createdAt,itemsSummary
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
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) continue;

                    // Try NEW format first (5 fields)
                    Order parsed = tryParseNewFormat(engine, trimmed);
                    if (parsed != null) {
                        loaded.add(parsed);
                        continue;
                    }

                    // Fallback to OLD format (4 fields)
                    parsed = tryParseOldFormat(engine, trimmed);
                    if (parsed != null) {
                        loaded.add(parsed);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return loaded;
    }

    /**
     * NEW format:
     *   username,orderId,total,createdAt,itemsSummary
     */
    private static Order tryParseNewFormat(StoreEngine engine, String line) {
        // split to 5: username, orderId, total, createdAt, rest(items)
        String[] parts = line.split(",", 5);
        if (parts.length < 5) return null;

        String username = unsafecsv(parts[0]).trim();
        if (username.isEmpty()) username = Order.UNKNOWN_CUSTOMER;

        int orderId;
        double total;
        try {
            orderId = Integer.parseInt(parts[1].trim());
            total = Double.parseDouble(parts[2].trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        LocalDateTime createdAt = parseDate(parts[3]);

        List<CartItem> items = parseItemsSummary(engine, unsafecsv(parts[4]).trim());

        // uses your new constructor Order(String username, int id, List<CartItem>, double, LocalDateTime)
        return new Order(username, orderId, items, total, createdAt);
    }

    /**
     * OLD format:
     *   orderId,total,createdAt,itemsSummary
     *
     * username will be UNKNOWN.
     */
    private static Order tryParseOldFormat(StoreEngine engine, String line) {
        String[] parts = line.split(",", 4);
        if (parts.length < 4) return null;

        int orderId;
        double total;
        try {
            orderId = Integer.parseInt(parts[0].trim());
            total = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        LocalDateTime createdAt = parseDate(parts[2]);

        List<CartItem> items = parseItemsSummary(engine, parts[3].trim());

        return new Order(orderId, items, total, createdAt); // backward-compatible constructor => UNKNOWN
    }

    private static LocalDateTime parseDate(String text) {
        try {
            return LocalDateTime.parse(text.trim());
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
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

            String productName = parts[0].trim();
            Product p = (engine == null) ? null : engine.findProductPublic(productName);
            if (p != null) {
                result.add(new CartItem(p, qty));
            }
        }

        return result;
    }

    /**
     * Very small "CSV safety":
     * we replace commas to keep the format stable.
     * (We intentionally keep it simple for this assignment.)
     */
    private static String safeCsv(String s) {
        if (s == null) return "";
        // replace commas so they won't break our split(",")
        return s.replace(",", " ");
    }

    private static String unsafecsv(String s) {
        return (s == null) ? "" : s;
    }
}
