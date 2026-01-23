/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

import java.awt.Color;

/**
 * Generic abstract builder for constructing {@link Product} instances.
 *
 * <p>
 * This class implements the Builder design pattern for common product fields and
 * supports fluent method chaining using the self-type (curiously recurring generic)
 * pattern.
 * </p>
 *
 * @param <T> the concrete product type produced by this builder
 * @param <B> the concrete builder type returned by fluent methods
 */
public abstract class ProductBuilder<T extends Product, B extends ProductBuilder<T, B>> {

    /** Product name. */
    private String name;

    /** Product price. */
    private double price;

    /** Initial stock quantity. */
    private int stock;

    /** Product description. */
    private String description;

    /** Product category. */
    private Category category;

    /** Product display color. */
    private Color color;

    /** Relative path to the product image resource. */
    private String imagePath;

    /**
     * Returns this builder instance as the concrete builder type.
     *
     * <p>
     * Subclasses implement this to return {@code this}. This enables fluent chaining
     * while preserving the concrete builder type.
     * </p>
     *
     * @return this builder instance
     */
    protected abstract B self();

    /**
     * Sets the product name.
     *
     * @param name the product name
     * @return this builder instance for method chaining
     */
    public B withName(String name) {
        this.name = name;
        return self();
    }

    /**
     * Sets the product price.
     *
     * @param price the product price
     * @return this builder instance for method chaining
     */
    public B withPrice(double price) {
        this.price = price;
        return self();
    }

    /**
     * Sets the initial stock quantity.
     *
     * @param stock the stock quantity
     * @return this builder instance for method chaining
     */
    public B withStock(int stock) {
        this.stock = stock;
        return self();
    }

    /**
     * Sets the product description.
     *
     * @param description the product description
     * @return this builder instance for method chaining
     */
    public B withDescription(String description) {
        this.description = description;
        return self();
    }

    /**
     * Sets the product category.
     *
     * @param category the product category
     * @return this builder instance for method chaining
     */
    public B withCategory(Category category) {
        this.category = category;
        return self();
    }

    /**
     * Sets the product display color.
     *
     * @param color the product color
     * @return this builder instance for method chaining
     */
    public B withColor(Color color) {
        this.color = color;
        return self();
    }

    /**
     * Sets the image path used to load the product image resource.
     *
     * @param imagePath relative image path in the resources
     * @return this builder instance for method chaining
     */
    public B withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return self();
    }

    /**
     * Returns the configured product name.
     *
     * @return the name value
     */
    protected String getName() {
        return name;
    }

    /**
     * Returns the configured product price.
     *
     * @return the price value
     */
    protected double getPrice() {
        return price;
    }

    /**
     * Returns the configured stock quantity.
     *
     * @return the stock value
     */
    protected int getStock() {
        return stock;
    }

    /**
     * Returns the configured product description.
     *
     * @return the description value
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns the configured product category.
     *
     * @return the category value
     */
    protected Category getCategory() {
        return category;
    }

    /**
     * Returns the configured product color.
     *
     * @return the color value
     */
    protected Color getColor() {
        return color;
    }

    /**
     * Returns the configured product image path.
     *
     * @return the image path value
     */
    protected String getImagePath() {
        return imagePath;
    }

    /**
     * Builds and returns a fully constructed product instance.
     *
     * @return the constructed concrete product
     */
    public abstract T build();
}
