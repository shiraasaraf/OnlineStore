/**
 * StoreEngine
 * -----------
 * Main engine class responsible for managing products, customers, and orders
 * within the online store system. Implements a Singleton pattern to ensure
 * there is only one instance of the engine throughout the application.
 *
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.engine;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.time.LocalDateTime;

import store.cart.Cart;
import store.cart.CartItem;
import store.products.*;
import store.order.Order;
import store.core.Customer;

public class StoreEngine {

    /** Singleton instance */
    private static StoreEngine instance = null;

    /** Store data collections */
    private List<Product> products;
    private List<Order> allOrders;
    private List<Customer> customers;

    /** Unique ID generator for orders */
    private static int nextOrderId = 0;

    /** Default catalog file that keeps the current product catalog between runs */
    private static final String DEFAULT_CATALOG_FILE = "products_catalog.csv";

    /** Default file used to append order history (CSV). */
    private static final String ORDER_HISTORY_FILE = "orders_history.csv";

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes collections for products, orders, and customers.
     */
    private StoreEngine() {
        this.products = new ArrayList<>();
        this.allOrders = new ArrayList<>();
        this.customers = new ArrayList<>();
    }

    /**
     * Returns the single instance of the StoreEngine.
     * Creates a new instance on first call.
     *
     * @return StoreEngine instance
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
     * Adds a product to the store's product list.
     * If a product with the same name already exists, its stock is increased
     * instead of adding a duplicate entry.
     *
     * @param p Product to add or merge
     */
    public void addProduct(Product p) {
        if (p == null) {
            return;
        }

        // ניסיון למצוא מוצר קיים עם אותו שם
        Product existing = findProductByName(p.getName());

        if (existing != null) {
            // אם קיים – נגדיל לו את המלאי
            // Product מממש StockManageable, לכן יש getStock() ו-increaseStock()
            int amountToAdd = p.getStock();
            if (amountToAdd > 0) {
                existing.increaseStock(amountToAdd);
            }
        } else {
            // אם לא קיים – פשוט מוסיפים לרשימת המוצרים
            products.add(p);
        }
    }


    /**
     * Returns a list of products that are currently in stock.
     *
     * @return List of available products
     */
    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();

        for (Product p : products) {
            if (p.getStock() > 0) {
                available.add(p);
            }
        }

        return available;   // FIXED: Previously returned "products" by mistake
    }

    /**
     * Returns a defensive copy of all products in the store (including out-of-stock).
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Removes a product from the catalog.
     *
     * @param product the product to remove
     * @return true if the product was removed, false otherwise
     */
    public boolean removeProduct(Product product) {
        if (product == null) {
            return false;
        }
        return products.remove(product);
    }


    /**
     * Loads products from a CSV/text file and adds them to the current catalog.
     * If a product with the same name already exists in the catalog,
     * its stock will be increased instead of creating a duplicate item.
     *
     * Expected format per line:
     * name,price,stock,description,category,imagePath
     */
    public void loadProductsFromFile(File file) throws IOException {
        if (file == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // skip empty lines
                }

                // skip header if present
                if (line.toLowerCase().startsWith("name,")) {
                    continue;
                }

                Product p = createProductFromCsvLine(line);
                if (p != null) {
                    addProduct(p);
                }
            }
        }
    }


    /**
     * Saves the current product catalog to a CSV/text file.
     * Format:
     * name,price,stock,description,category,imagePath
     */
    public void saveProductsToFile(File file) throws IOException {
        if (file == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // optional header
            writer.write("name,price,stock,description,category,imagePath");
            writer.newLine();

            for (Product p : products) {
                String safeDescription = p.getDescription();
                if (safeDescription == null) {
                    safeDescription = "";
                }
                // very simple CSV handling: replace commas in description
                safeDescription = safeDescription.replace(",", " ");

                String line = String.format(
                        "%s,%f,%d,%s,%s,%s",
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        safeDescription,
                        p.getCategory().name(),
                        ""  // imagePath placeholder for now
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Helper: parse a single CSV line and create an appropriate Product instance.
     */
    private Product createProductFromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) {
            return null; // invalid line
        }

        String name = parts[0].trim();
        double price;
        int stock;

        try {
            price = Double.parseDouble(parts[1].trim());
            stock = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            return null; // invalid numeric values
        }

        String description = parts[3].trim();
        String categoryStr = parts[4].trim().toUpperCase();
        Category category;

        try {
            category = Category.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            category = Category.ELECTRONICS; // sensible default
        }

        // imagePath is currently ignored at the model level; it can be used by the GUI later.
        // String imagePath = parts.length >= 6 ? parts[5].trim() : null;

        // Create a concrete product type based on the category.
        switch (category) {
            case BOOKS:
                return new BookProduct(
                        name,
                        price,
                        stock,
                        description,
                        category,
                        java.awt.Color.WHITE,
                        "Unknown author",
                        100
                );
            case CLOTHING:
                return new ClothingProduct(
                        name,
                        price,
                        stock,
                        description,
                        category,
                        java.awt.Color.LIGHT_GRAY,
                        "M"
                );
            case ELECTRONICS:
            default:
                return new ElectronicsProduct(
                        name,
                        price,
                        stock,
                        description,
                        category,
                        java.awt.Color.DARK_GRAY,
                        12,
                        "Generic Brand"
                );
        }
    }

    // ------------------------------------------------------------------------
    // Customer Management
    // ------------------------------------------------------------------------

    /**
     * Registers a new customer if the username is not already taken.
     *
     * @param c Customer to register
     * @return true if registered successfully, false if username already exists
     */
    public boolean registerCustomer(Customer c) {
        if (c == null) {
            return false;
        }

        for (Customer existing : customers) {
            if (existing.getUsername().equals(c.getUsername())) {
                return false; // Username already taken
            }
        }

        customers.add(c);
        return true;
    }

    // ------------------------------------------------------------------------
    // Order Management
    // ------------------------------------------------------------------------

    /**
     * Returns a defensive copy of all orders in the system.
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(allOrders);
    }

    /**
     * Creates a new order based on the contents of a given cart.
     * Generates a unique order ID, adds the order to the system,
     * and clears the cart afterward.
     *
     * @param cart Cart to create order from
     * @return the created Order object, or null if the cart is empty
     */
    public Order createOrderFromCart(Cart cart) {

        if (cart == null || cart.isEmpty()) {
            return null;
        }

        nextOrderId++;    // Generate unique order ID

        Order newOrder = new Order(
                nextOrderId,
                cart.getItems(),
                cart.calculateTotal()
        );

        allOrders.add(newOrder);
        cart.clear();     // Empty cart after successful order

        // Persist order details to a history file (best-effort).
        appendOrderToHistoryFile(newOrder);

        return newOrder;
    }

    /**
     * Appends a single order record to the history CSV file.
     * Format:
     * orderId,totalAmount,dateTime,itemsSummary
     */
    private void appendOrderToHistoryFile(Order order) {
        if (order == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {

            StringBuilder itemsSummary = new StringBuilder();
            for (CartItem item : order.getItems()) {
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
                    LocalDateTime.now().toString(),
                    itemsSummary.toString()
            );

            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            // For this exercise we simply print the stack trace.
            // In a real app we might log this instead.
            e.printStackTrace();
        }

    }

    /**
     * Loads the default product catalog from the file system if the file exists.
     * This method is used to restore the catalog when the application starts.
     */
    public void loadDefaultCatalogIfExists() {
        File file = new File(DEFAULT_CATALOG_FILE);
        if (file.exists() && file.isFile()) {
            try {
                loadProductsFromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the current product catalog to the default catalog file.
     * Call this after any change to the catalog (for example, after loading
     * products from an external CSV file).
     */
    public void saveCatalogToDefaultFile() {
        File file = new File(DEFAULT_CATALOG_FILE);
        try {
            saveProductsToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds an existing product in the catalog by name (case-insensitive).
     *
     * @param name the product name to search for
     * @return the existing Product with the same name, or null if not found
     */
    private Product findProductByName(String name) {
        if (name == null) {
            return null;
        }

        for (Product p : products) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }

        return null;
    }


}
