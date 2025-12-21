/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents a clothing product.
 * <p>
 * Extends {@link Product} with a clothing-specific size attribute.
 * </p>
 */
public class ClothingProduct extends Product {

    /** Clothing size. */
    private String size;

    /**
     * Constructs a new clothing product.
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock
     * @param description product description
     * @param category    product category
     * @param color       product color
     * @param imagePath   relative image path
     * @param size        clothing size
     */
    public ClothingProduct(String name, double price, int stock, String description,
                           Category category, Color color, String imagePath, String size) {

        super(name, price, stock, description, category, color, imagePath);

        this.size = "Unknown size";

        if (size != null && !size.trim().isEmpty()) {
            this.size = size.trim();
        }
    }

    /**
     * Returns the clothing size.
     *
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * Returns a string representation of this clothing product.
     *
     * @return clothing product details
     */
    @Override
    public String toString() {
        return "Clothing Product\n" +
                super.toString() + "\n" +
                "Size: " + getSize();
    }

    /**
     * Compares this clothing product to another object.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
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
