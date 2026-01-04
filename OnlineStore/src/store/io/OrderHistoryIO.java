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
 * Utility class responsible for persisting and loading order history to/from a CSV file.
 * <p>
 * This class centralizes all file I/O for orders and uses a single lock object to prevent
 * concurrent reads/writes that could corrupt the file when multiple windows/threads operate
 * in parallel.
 * </p>
 *
 * <h3>Supported CSV Formats</h3>
 * <p>
 * Two formats are supported for backward compatibility:
 * </p>
 * <ol>
 *   <li><b>New format (recommended)</b>: {@code username,orderId,total,createdAt,itemsSummary}</li>
 *   <li><b>Old format</b>: {@code orderId,total,createdAt,itemsSummary} (username is treated as unknown)</li>
 * </ol>
 *
 * <p>
 * The {@code itemsSummary} field uses a simple semicolon-separated representation:
 * {@code "ProductName xQTY;ProductName xQTY;"}.
 * </p>
 */
public class OrderHistoryIO {

    /** Global lock used to serialize access to the order history file. */
    private static final Object ORDER_FILE_LOCK = new Object();

    /**
     * Default CSV file path for order history.
     * <p>
     * The path is relative to the application's current working directory.
     * </p>
     */
    public static final String ORDER_HISTORY_FILE = "orders_history.csv";

    /**
     * Appends a single order record to the history file using the new format.
     * <p>
     * Written format:
     * {@code username,orderId,total,createdAt,itemsSummary}
     * </p>
     * <p>
     * This method synchronizes on {@link #ORDER_FILE_LOCK} to prevent concurrent writes.
     * If an I/O error occurs, the exception is printed (kept simple for this assignment).
     * </p>
     *
     * @param order the order to append; if {@code null}, the method returns without writing
     */
    public static void appendOrder(Order order) {
        if (order == null) return;

        synchronized (ORDER_FILE_LOCK) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {

                String itemsSummary = buildItemsSummary(order);

                // username first (so admin/history can filter + show owner)
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

    /**
     * Builds a compact textual summary of the items in an order.
     * <p>
     * The summary format is a semicolon-separated list where each entry represents
     * a product name and its quantity:
     * {@code "ProductName xQTY;ProductName xQTY;"}.
     * </p>
     * <p>
     * Null {@link CartItem} entries and entries with {@code null} products are skipped.
     * </p>
     *
     * @param order the order whose items should be summarized (assumed non-null)
     * @return an items summary string (may be empty if no valid items exist)
     */
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
     * Loads order history from the default history file if it exists.
     * <p>
     * Supports both the new and old CSV formats. When parsing items, this method can use
     * the provided {@link StoreEngine} to resolve product names into actual {@link Product}
     * objects so that {@link CartItem} references real products.
     * </p>
     * <p>
     * If the file does not exist, an empty list is returned.
     * This method synchronizes on {@link #ORDER_FILE_LOCK} to prevent reading during a write.
     * </p>
     *
     * @param engine the engine used to resolve products by name (may be {@code null})
     * @return a list of loaded orders (never {@code null})
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
     * Attempts to parse a CSV line using the new format:
     * {@code username,orderId,total,createdAt,itemsSummary}.
     *
     * @param engine engine used to resolve product names (may be {@code null})
     * @param line   CSV line to parse
     * @return parsed order, or {@code null} if the line does not match this format
     */
    private static Order tryParseNewFormat(StoreEngine engine, String line) {
        // split to 5: username, orderId, total, createdAt, rest(items)
        String[] parts = line.split(",", 5);
        if (parts.length < 5) return null;

        String username = unsafeCsv(parts[0]).trim();
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

        List<CartItem> items = parseItemsSummary(engine, unsafeCsv(parts[4]).trim());

        // uses constructor: Order(String username, int id, List<CartItem>, double, LocalDateTime)
        return new Order(username, orderId, items, total, createdAt);
    }

    /**
     * Attempts to parse a CSV line using the old format:
     * {@code orderId,total,createdAt,itemsSummary}.
     * <p>
     * In this format, the username is not stored and will be treated as unknown.
     * </p>
     *
     * @param engine engine used to resolve product names (may be {@code null})
     * @param line   CSV line to parse
     * @return parsed order, or {@code null} if the line does not match this format
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

        // backward-compatible constructor => UNKNOWN customer
        return new Order(orderId, items, total, createdAt);
    }

    /**
     * Parses a {@link LocalDateTime} value from text.
     * <p>
     * If parsing fails, {@link LocalDateTime#now()} is returned (kept simple for this assignment).
     * </p>
     *
     * @param text textual date-time representation
     * @return parsed date-time, or {@link LocalDateTime#now()} on failure
     */
    private static LocalDateTime parseDate(String text) {
        try {
            return LocalDateTime.parse(text.trim());
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }

    /**
     * Parses an items summary string into a list of {@link CartItem}.
     * <p>
     * The expected format is: {@code "ProductName xQTY;ProductName xQTY;"}.
     * Product names are resolved via {@link StoreEngine#findProductPublic(String)} when an engine is provided.
     * Items that cannot be parsed or resolved are skipped.
     * </p>
     *
     * @param engine  engine used to resolve product names (may be {@code null})
     * @param summary items summary string
     * @return list of parsed cart items (never {@code null})
     */
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
     * Minimal CSV safety helper.
     * <p>
     * Replaces commas with spaces so that fields do not break a simple {@code split(",")}
     * parsing approach. This is intentionally kept simple for the assignment and is not a
     * full CSV escaping implementation.
     * </p>
     *
     * @param s input string (may be {@code null})
     * @return a string safe to embed in our simplified CSV format
     */
    private static String safeCsv(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }

    /**
     * Reverses {@link #safeCsv(String)} if needed.
     * <p>
     * Currently, {@link #safeCsv(String)} replaces commas with spaces, so this method
     * simply returns a non-null string. It exists for symmetry and future extension.
     * </p>
     *
     * @param s stored CSV field value
     * @return non-null field value
     */
    private static String unsafeCsv(String s) {
        return (s == null) ? "" : s;
    }
}
