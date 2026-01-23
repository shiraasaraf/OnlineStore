/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.io;

import store.products.*;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading and saving the product catalog in CSV format.
 *
 * <p>
 * The CSV format uses the following columns:
 * {@code name,price,stock,description,category,imagePath}.
 * The first row may contain the header and will be ignored when loading.
 * </p>
 *
 * <p>
 * When loading, each CSV row is converted into a {@link Product} instance using
 * {@link ProductFactory} (no direct product constructors are used). Missing or invalid
 * values are handled defensively by skipping the row or applying defaults.
 * </p>
 */
public final class ProductCatalogIO {

    /**
     * Prevents instantiation; this is a static utility class.
     */
    private ProductCatalogIO() {}

    /**
     * Expected CSV header line.
     */
    private static final String HEADER =
            "name,price,stock,description,category,imagePath";

    /**
     * Default image path used when no image path is provided in the CSV row.
     */
    private static final String DEFAULT_IMAGE = "images/default.jpg";

    /**
     * Loads products from a CSV file.
     *
     * <p>
     * Empty lines are ignored. The header row (or any row starting with {@code "name,"})
     * is skipped. Lines that cannot be parsed are ignored.
     * </p>
     *
     * @param file the CSV file to load from
     * @return a list of parsed products (empty if {@code file} is {@code null} or no products were loaded)
     * @throws IOException if reading from the file fails
     */
    public static List<Product> loadProductsFromFile(File file) throws IOException {
        List<Product> result = new ArrayList<>();
        if (file == null) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase(HEADER) ||
                        line.toLowerCase().startsWith("name,")) {
                    continue;
                }

                Product p = parseProductLine(line);
                if (p != null) {
                    result.add(p);
                }
            }
        }

        return result;
    }

    /**
     * Saves the given products list into a CSV file.
     *
     * <p>
     * The file is overwritten. A header row is always written. Null products are skipped.
     * </p>
     *
     * @param file     the destination CSV file
     * @param products the products to write (may be {@code null})
     * @throws IOException if writing to the file fails
     */
    public static void saveProductsToFile(File file,
                                          List<Product> products) throws IOException {
        if (file == null) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            if (products == null) return;

            for (Product p : products) {
                if (p == null) continue;
                writer.write(productToCsvLine(p));
                writer.newLine();
            }
        }
    }

    /**
     * Parses a single CSV line into a {@link Product}.
     *
     * <p>
     * The expected order is:
     * {@code name,price,stock,description,category[,imagePath]}.
     * If {@code category} cannot be parsed, a default category is used.
     * If {@code imagePath} is missing/blank, a default image path is used.
     * </p>
     *
     * @param line the raw CSV line
     * @return a constructed {@link Product}, or {@code null} if parsing fails
     */
    private static Product parseProductLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) return null;

        String name = parts[0].trim();

        double price;
        int stock;
        try {
            price = Double.parseDouble(parts[1].trim());
            stock = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            return null;
        }

        String description = parts[3].trim();

        Category category;
        try {
            category = Category.valueOf(parts[4].trim().toUpperCase());
        } catch (Exception e) {
            category = Category.ELECTRONICS;
        }

        String imagePath = DEFAULT_IMAGE;
        if (parts.length >= 6 && !parts[5].trim().isEmpty()) {
            imagePath = parts[5].trim();
        }

        Color color = (category == Category.BOOKS)
                ? Color.WHITE
                : (category == Category.CLOTHING ? Color.LIGHT_GRAY : Color.DARK_GRAY);

        ProductFactory.BasicFields basic = new ProductFactory.BasicFields(
                name, price, stock, description, category, color, imagePath
        );
        return ProductFactory.createProductWithDefaults(basic);
    }

    /**
     * Converts a {@link Product} to a single CSV line matching the catalog format.
     *
     * <p>
     * Commas in the description are replaced with spaces to preserve the simple CSV layout.
     * </p>
     *
     * @param p the product to serialize
     * @return a CSV line representing the product
     */
    private static String productToCsvLine(Product p) {
        String name = safe(p.getName());
        String description = safe(p.getDescription()).replace(",", " ");
        String category = (p.getCategory() == null)
                ? Category.ELECTRONICS.name()
                : p.getCategory().name();
        String imagePath = safe(p.getImagePath());

        return String.format(
                "%s,%.2f,%d,%s,%s,%s",
                name,
                p.getPrice(),
                p.getStock(),
                description,
                category,
                imagePath
        );
    }

    /**
     * Returns a trimmed string value, or an empty string for {@code null}.
     *
     * @param s the input string
     * @return a non-null, trimmed string
     */
    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
