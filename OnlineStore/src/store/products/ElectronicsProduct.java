/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;

/**
 * Represents an electronic product in the store.
 * Extends {@link Product} by adding electronic-specific fields:
 * warranty period (in months) and brand name.
 *
 * All validation and default values ensure that the object is always
 * created in a valid state. Warranty must be a positive number, and
 * the brand cannot be null or empty.
 */
public class ElectronicsProduct extends Product{

    private int warrantMonths;
    private String brand;

    /**
     * Constructs a new ElectronicsProduct with validation.
     * Invalid or null parameters are replaced with default values.
     *
     * @param name            product name (default if null or empty)
     * @param price           product price (default if not positive)
     * @param stock           initial stock (default if negative)
     * @param description     product description (empty string if null)
     * @param category        product category (default if null)
     * @param color           product color (default if null)
     * @param warrantMonths   warranty period in months (must be positive)
     * @param brand           brand name (default if null or empty)
     */
    public ElectronicsProduct(String name, double price, int stock, String description,
                              Category category, Color color, int warrantMonths, String brand){

        super(name, price, stock, description, category, color);

        //default values
        this.warrantMonths = 1;
        this.brand = "Unknown brand";

        // warrantMonths – must be positive
        if (warrantMonths > 0) {
            this.warrantMonths = warrantMonths;
        }

        // brand – only if non-null and not blank
        if (brand != null && !brand.trim().isEmpty()) {
            this.brand = brand;
        }

    }

    //---------------------------------------------------------------------------------------------

    /**
     * Returns the warranty period of this product, in months.
     *
     * @return warranty duration in months
     */
    public int getWarrantMonths() {
        return this.warrantMonths;
    }

    /**
     * Returns the brand of this electronic product.
     *
     * @return the brand name
     */
    public String getBrand() {
        return this.brand;
    }

    //---------------------------------------------------------------------------------------------

    /**
     * Returns a multi-line string describing this electronic product,
     * including general product details and electronics-specific fields
     * such as warranty and brand.
     *
     * @return string representation of this electronic product
     */
    @Override
    public String toString() {
        return "Electronics Product\n" +
                super.toString() + "\n" +
                "Warranty Months: " + getWarrantMonths() + "\n" +
                "Brand: " + getBrand();
    }

    /**
     * Checks whether this electronics product is equal to another object.
     *
     * Two ElectronicsProduct objects are considered equal if:
     * 1. Both objects are instances of ElectronicsProduct.
     * 2. The Product fields (name and category) are equal, based on super.equals(o).
     * 3. They have the same brand.
     *
     * @param o the object to compare with this electronics product
     * @return true if both objects represent the same electronics product;
     *         false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElectronicsProduct)) return false;

        if (!super.equals(o)) return false; // name + category

        ElectronicsProduct other = (ElectronicsProduct) o;
        return java.util.Objects.equals(this.brand, other.brand);
    }
}

