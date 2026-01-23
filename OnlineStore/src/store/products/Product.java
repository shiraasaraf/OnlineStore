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
 * Abstract base class for all store products.
 *
 * <p>
 * This class encapsulates common product attributes such as name, price, stock,
 * description, category, color and image path. It provides basic validation logic
 * and default values for invalid or missing inputs.
 * </p>
 *
 * <p>
 * Products are intended to be created via {@link ProductFactory} and the
 * corresponding {@link ProductBuilder} implementations.
 * </p>
 */
public abstract class Product
        implements StoreEntity, PricedItem, StockManageable, Persistable {

    /** Product name. */
    private String name;

    /** Product price (must be positive). */
    private double price;

    /** Available stock quantity (non-negative). */
    private int stock;

    /** Product description (never {@code null}). */
    private String description;

    /** Product category. */
    private Category category;

    /** Display color associated with the product. */
    private Color color;

    /** Relative path to the product image resource. */
    private String imagePath;

    /**
     * Package-private constructor used by product builders.
     *
     * <p>
     * Products must be instantiated via {@link ProductFactory} and concrete
     * {@link ProductBuilder} implementations. Direct construction outside the
     * {@code store.products} package is intentionally restricted.
     * </p>
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock quantity
     * @param description product description
     * @param category    product category
     * @param color       product display color
     * @param imagePath   path to the product image resource
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

    /**
     * Returns the product name.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the product description.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the product category.
     *
     * @return the product category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the display color associated with the product.
     *
     * @return the product color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the stock quantity.
     *
     * @param stock new stock value (must be non-negative)
     * @return {@code true} if the stock was updated; {@code false} otherwise
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
     * @return {@code true} if the description was updated; {@code false} otherwise
     */
    protected boolean setDescription(String description) {
        if (description == null) {
            return false;
        }
        this.description = description;
        return true;
    }

    /**
     * Persists this product to storage.
     *
     * <p>
     * Persistence is not implemented in the current version.
     * </p>
     *
     * @param path target file path
     */
    @Override
    public void saveToFile(String path) {
        // Not implemented in this version.
    }

    /**
     * Returns a display name suitable for UI components.
     *
     * @return the display name
     */
    @Override
    public String getDisplayName() {
        return getName();
    }

    /**
     * Returns a multi-line string describing the product details.
     *
     * @return formatted product details
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
     * @return the price value
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price new price (must be positive)
     * @return {@code true} if the price was updated; {@code false} otherwise
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
     * Returns the current stock quantity.
     *
     * @return available stock
     */
    @Override
    public int getStock() {
        return stock;
    }

    /**
     * Increases the product stock by the given amount.
     *
     * @param amount amount to add (must be positive)
     * @return {@code true} if the stock was increased; {@code false} otherwise
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
     * Decreases the product stock by the given amount.
     *
     * @param amount amount to subtract (must be positive)
     * @return {@code true} if the stock was decreased; {@code false} otherwise
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
     * Returns a concise string representation of the product.
     *
     * @return formatted product string
     */
    @Override
    public String toString() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Stock: " + getStock();
    }

    /**
     * Compares this product to another object for equality.
     *
     * <p>
     * Two products are considered equal if they share the same name and category.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the products are equal; {@code false} otherwise
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
     * Returns the image path associated with this product.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }
}
