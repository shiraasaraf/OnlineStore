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
     * Creates a product with validated values and sensible defaults.
     *
     * @param name        product name (default used if null/blank)
     * @param price       product price (default used if not positive)
     * @param stock       initial stock (default used if negative)
     * @param description product description (empty string used if null)
     * @param category    product category (default used if null)
     * @param color       product color (default used if null)
     * @param imagePath   relative image path (default used if null/blank)
     */
    public Product(String name, double price, int stock, String description,
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

    /**
     * Returns the product name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the product description.
     *
     * @return the description (never null)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the product category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the product color.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the stock value.
     *
     * @param stock the new stock
     * @return true if assigned, false otherwise
     */
    private boolean setStock(int stock) {
        if (stock < 0) {
            return false;
        }
        this.stock = stock;
        return true;
    }

    /**
     * Sets the product description.
     *
     * @param description the new description
     * @return true if assigned, false otherwise
     */
    protected boolean setDescription(String description) {
        if (description == null) {
            return false;
        }
        this.description = description;
        return true;
    }

    /**
     * Saves this product to a file.
     *
     * @param path destination path
     */
    @Override
    public void saveToFile(String path) {
        // Not implemented in this version.
    }

    /**
     * Returns a short name for UI display.
     *
     * @return display name
     */
    @Override
    public String getDisplayName() {
        return getName();
    }

    /**
     * Returns product details for UI display.
     *
     * @return multi-line details string
     */
    @Override
    public String getDisplayDetails() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Description: " + getDescription() + "\n" +
                "Color: " + getColor() + "\n" +
                "Image: " + getImagePath() + "\n";
    }

    /**
     * Returns the product price.
     *
     * @return the price
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price the new price
     * @return true if assigned, false otherwise
     */
    @Override
    public boolean setPrice(double price) {
        if (price <= 0) {
            return false;
        }
        this.price = price;
        return true;
    }

    /**
     * Returns the current stock.
     *
     * @return stock amount
     */
    @Override
    public int getStock() {
        return stock;
    }

    /**
     * Increases stock by the given amount.
     *
     * @param amount amount to add
     * @return true if updated, false otherwise
     */
    @Override
    public boolean increaseStock(int amount) {
        if (amount <= 0) {
            return false;
        }
        stock += amount;
        return true;
    }

    /**
     * Decreases stock by the given amount.
     *
     * @param amount amount to subtract
     * @return true if updated, false otherwise
     */
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

    /**
     * Returns a short textual representation of the product.
     *
     * @return product string
     */
    @Override
    public String toString() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Stock: " + getStock();
    }

    /**
     * Compares products by class, name and category.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product other = (Product) o;
        return Objects.equals(this.name, other.name) &&
                this.category == other.category;
    }

    /**
     * Returns the image path.
     *
     * @return relative image path
     */
    public String getImagePath() {
        return imagePath;
    }
}
