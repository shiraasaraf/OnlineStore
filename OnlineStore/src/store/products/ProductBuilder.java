/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

import java.awt.Color;

/**
 * Abstract Builder for {@link Product} objects.
 *
 * <p>
 * Implements the <b>Builder Design Pattern</b>.
 * Stores common product fields and provides fluent "withX(...)" methods.
 * </p>
 *
 * <p>
 * NOTE (course rule): all data members are <b>private</b>.
 * Subclasses access them only via protected getters (no protected fields).
 * </p>
 *
 * @param <T> concrete product type
 * @param <B> concrete builder type (self-type pattern)
 */
public abstract class ProductBuilder<T extends Product, B extends ProductBuilder<T, B>> {

    // common product fields (private per course rules)
    private String name;
    private double price;
    private int stock;
    private String description;
    private Category category;
    private Color color;
    private String imagePath;

    /**
     * Returns {@code this} casted to the concrete builder type.
     * Used to support fluent method chaining.
     */
    protected abstract B self();

    public B withName(String name) {
        this.name = name;
        return self();
    }

    public B withPrice(double price) {
        this.price = price;
        return self();
    }

    public B withStock(int stock) {
        this.stock = stock;
        return self();
    }

    public B withDescription(String description) {
        this.description = description;
        return self();
    }

    public B withCategory(Category category) {
        this.category = category;
        return self();
    }

    public B withColor(Color color) {
        this.color = color;
        return self();
    }

    public B withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return self();
    }

    // protected getters (allowed; not data members)
    protected String getName() {
        return name;
    }

    protected double getPrice() {
        return price;
    }

    protected int getStock() {
        return stock;
    }

    protected String getDescription() {
        return description;
    }

    protected Category getCategory() {
        return category;
    }

    protected Color getColor() {
        return color;
    }

    protected String getImagePath() {
        return imagePath;
    }

    /**
     * Builds and returns a fully constructed {@link Product} instance.
     *
     * @return concrete product
     */
    public abstract T build();
}
