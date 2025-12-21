/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents an electronics product.
 * <p>
 * Extends {@link Product} with electronics-specific fields such as
 * warranty period and brand name.
 * </p>
 */
public class ElectronicsProduct extends Product {

    /** Warranty period in months. */
    private int warrantMonths;

    /** Brand name. */
    private String brand;

    /**
     * Constructs a new electronics product.
     *
     * @param name          product name
     * @param price         product price
     * @param stock         initial stock
     * @param description   product description
     * @param category      product category
     * @param color         product color
     * @param imagePath     relative image path
     * @param warrantMonths warranty period in months
     * @param brand         brand name
     */
    public ElectronicsProduct(String name, double price, int stock, String description,
                              Category category, Color color, String imagePath,
                              int warrantMonths, String brand) {

        super(name, price, stock, description, category, color, imagePath);

        this.warrantMonths = 1;
        this.brand = "Unknown brand";

        if (warrantMonths > 0) {
            this.warrantMonths = warrantMonths;
        }

        if (brand != null && !brand.trim().isEmpty()) {
            this.brand = brand;
        }
    }

    /**
     * Returns the warranty period.
     *
     * @return warranty duration in months
     */
    public int getWarrantMonths() {
        return warrantMonths;
    }

    /**
     * Returns the product brand.
     *
     * @return brand name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns a string representation of this electronics product.
     *
     * @return electronics product details
     */
    @Override
    public String toString() {
        return "Electronics Product\n" +
                super.toString() + "\n" +
                "Warranty Months: " + getWarrantMonths() + "\n" +
                "Brand: " + getBrand();
    }

    /**
     * Compares this electronics product to another object.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElectronicsProduct)) return false;
        if (!super.equals(o)) return false;

        ElectronicsProduct other = (ElectronicsProduct) o;
        return Objects.equals(this.brand, other.brand);
    }
}
