/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents a clothing product in the store catalog.
 *
 * <p>
 * A {@code ClothingProduct} extends {@link Product} by adding clothing-specific
 * attributes, such as size.
 * </p>
 *
 * <p>
 * Instances of this class are intended to be created via {@link ProductFactory}
 * and not directly by client code.
 * </p>
 */
public class ClothingProduct extends Product {

    /** Size descriptor of the clothing item. */
    private String size;

    /**
     * Constructs a new clothing product.
     *
     * <p>
     * If the provided size is {@code null} or blank, a default value is used.
     * </p>
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock quantity
     * @param description textual description of the product
     * @param category    product category (expected to be {@link Category#CLOTHING})
     * @param color       display color associated with the product
     * @param imagePath   path to the product image resource
     * @param size        size of the clothing item
     */
    ClothingProduct(String name, double price, int stock, String description,
                    Category category, Color color, String imagePath, String size) {

        super(name, price, stock, description, category, color, imagePath);

        this.size = "Unknown size";

        if (size != null && !size.trim().isEmpty()) {
            this.size = size.trim();
        }
    }

    /**
     * Returns the size of the clothing item.
     *
     * @return the size descriptor
     */
    public String getSize() {
        return size;
    }

    /**
     * Returns a human-readable string representation of this clothing product.
     *
     * @return a formatted string describing the clothing product
     */
    @Override
    public String toString() {
        return "Clothing Product\n" +
                super.toString() + "\n" +
                "Size: " + getSize();
    }

    /**
     * Compares this clothing product to another object for equality.
     *
     * <p>
     * Two {@code ClothingProduct} instances are considered equal if their base
     * {@link Product} properties are equal and they have the same size.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClothingProduct)) return false;
        if (!super.equals(o)) return false;

        ClothingProduct other = (ClothingProduct) o;
        return Objects.equals(this.size, other.size);
    }
}
