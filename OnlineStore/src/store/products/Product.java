/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import store.core.Persistable;
import store.core.StoreEntity;

import java.awt.Color;
import java.util.Objects;

/**
 * Abstract base class for store products.
 * <p>
 * Holds common product data (name, price, stock, description, category, color, image path)
 * and provides basic validation and default values.
 * </p>
 */
public abstract class Product implements StoreEntity, PricedItem, StockManageable, Persistable {

    /** Product name. */
    private String name;

    /** Product price (positive). */
    private double price;

    /** Available stock (non-negative). */
    private int stock;

    /** Product description (never {@code null}). */
    private String description;

    /** Product category. */
    private Category category;

    /** Product color. */
    private Color color;

    /** Relative path to the product image in resources. */
    private String imagePath;

    /**
     * Package-private constructor.
     * <p>
     * <b>Design Patterns:</b> Builder + Factory Method.
     * Products must be created via {@link ProductFactory} and the relevant
     * {@link ProductBuilder} implementation (not via {@code new} outside the
     * {@code store.products} package).
     * </p>
     */
    Product(String name, double price, int stock, String description,
            Category category, Color color, String imagePath) {

        this.name = "Unknown product";
        this.price = 0.1;
        this.stock = 0;
        this.description = "";
        this.category = Category.BOOKS;
        this.color = Color.BLACK;
        this.imagePath = "images/default.jpg";

        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (category != null) {
            this.category = category;
        }
        if (color != null) {
            this.color = color;
        }
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            this.imagePath = imagePath.trim();
        }

        setPrice(price);
        setStock(stock);
        setDescription(description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public Color getColor() {
        return color;
    }

    private boolean setStock(int stock) {
        if (stock < 0) {
            return false;
        }
        this.stock = stock;
        return true;
    }

    protected boolean setDescription(String description) {
        if (description == null) {
            return false;
        }
        this.description = description;
        return true;
    }

    @Override
    public void saveToFile(String path) {
        // Not implemented in this version.
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDisplayDetails() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Description: " + getDescription() + "\n" +
                "Color: " + getColor() + "\n" +
                "Image: " + getImagePath() + "\n";
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public boolean setPrice(double price) {
        if (price <= 0) {
            return false;
        }
        this.price = price;
        return true;
    }

    @Override
    public int getStock() {
        return stock;
    }

    @Override
    public boolean increaseStock(int amount) {
        if (amount <= 0) {
            return false;
        }
        stock += amount;
        return true;
    }

    @Override
    public boolean decreaseStock(int amount) {
        if (amount <= 0) {
            return false;
        }
        if (stock - amount < 0) {
            return false;
        }
        stock -= amount;
        return true;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Stock: " + getStock();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product other = (Product) o;
        return Objects.equals(this.name, other.name) &&
                this.category == other.category;
    }

    public String getImagePath() {
        return imagePath;
    }
}
