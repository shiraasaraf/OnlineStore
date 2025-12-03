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
     * Uses the equality definition from Product:
     * products are equal if they have the same name and category.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
