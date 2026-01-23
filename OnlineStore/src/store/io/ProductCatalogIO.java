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

public final class ProductCatalogIO {

    private ProductCatalogIO() {}

    private static final String HEADER =
            "name,price,stock,description,category,imagePath";

    private static final String DEFAULT_IMAGE = "images/default.jpg";

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

        // Creation must go through Factory + Builder (no direct constructors).
        Color color = (category == Category.BOOKS)
                ? Color.WHITE
                : (category == Category.CLOTHING ? Color.LIGHT_GRAY : Color.DARK_GRAY);

        ProductFactory.BasicFields basic = new ProductFactory.BasicFields(
                name, price, stock, description, category, color, imagePath
        );
        return ProductFactory.createProductWithDefaults(basic);
    }

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

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
