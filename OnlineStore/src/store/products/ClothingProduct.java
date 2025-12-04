/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;

/**
 * Represents a clothing product in the store.
 * Extends {@link Product} by adding a clothing-specific field: size.
 *
 * This class adds additional details relevant only to clothing products
 * while keeping general validation and behavior inside the parent class Product.
 */
public class ClothingProduct extends Product {

    //data member
    private String size;

    /**
     * Constructs a new ClothingProduct with validation.
     * Invalid or null parameters are replaced with default values.
     *
     * @param name        product name (default if null or empty)
     * @param price       product price (default if not positive)
     * @param stock       initial stock (default if negative)
     * @param description product description (empty string if null)
     * @param category    product category (default if null)
     * @param color       product color (default if null)
     * @param size        clothing size (default if null or empty)
     */
    public ClothingProduct(String name, double price, int stock, String description,
                           Category category, Color color, String size) {

        super(name, price, stock, description, category, color);

        //default value
        this.size = "Unknown size";

        // size – only if non-null and not blank
        if (size != null && !size.trim().isEmpty()) {
            this.size = size;
        }

    }

    //-----------------------------------------------------------------------------------------

    /**
     * Returns the clothing size.
     *
     * @return the size value
     */
    public String getSize() {
        return this.size;
    }

    //------------------------------------------------------------------------------------------

    /**
     * Returns a multi-line string describing this clothing product,
     * including all product details and the clothing-specific size field.
     *
     * @return string representation of this clothing product
     */
    @Override
    public String toString() {
        return "Clothing Product\n" +
                super.toString() + "\n" +
                "Size: " + getSize();
    }


    /**
     * Checks whether this clothing product is equal to another object.
     *
     * Two ClothingProduct objects are considered equal if:
     * 1. Both objects are instances of ClothingProduct.
     * 2. The Product fields (name and category) are equal, using super.equals(o).
     * 3. They have the same size.
     *
     * @param o the object to compare with this clothing product
     * @return true if both objects represent the same clothing product;
     *         false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClothingProduct)) return false;

        if (!super.equals(o)) return false;

        ClothingProduct other = (ClothingProduct) o;

        return java.util.Objects.equals(this.size, other.size);
    }
}
