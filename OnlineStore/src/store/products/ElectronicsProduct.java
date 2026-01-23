/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents an electronics product in the store catalog.
 *
 * <p>
 * An {@code ElectronicsProduct} extends {@link Product} by adding
 * electronics-specific attributes such as warranty duration and brand.
 * </p>
 *
 * <p>
 * Instances of this class are intended to be created via {@link ProductFactory}
 * and not directly by client code.
 * </p>
 */
public class ElectronicsProduct extends Product {

    /** Warranty duration in months. */
    private int warrantMonths;

    /** Brand name of the electronic product. */
    private String brand;

    /**
     * Constructs a new electronics product.
     *
     * <p>
     * If the provided warranty duration is non-positive, a default value is used.
     * If the provided brand is {@code null} or blank, a default value is used.
     * </p>
     *
     * @param name           product name
     * @param price          product price
     * @param stock          initial stock quantity
     * @param description    textual description of the product
     * @param category       product category (expected to be {@link Category#ELECTRONICS})
     * @param color          display color associated with the product
     * @param imagePath      path to the product image resource
     * @param warrantMonths  warranty duration in months
     * @param brand          brand name
     */
    ElectronicsProduct(String name, double price, int stock, String description,
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
     * Returns the warranty duration in months.
     *
     * @return the warranty duration
     */
    public int getWarrantMonths() {
        return warrantMonths;
    }

    /**
     * Returns the brand name of the electronic product.
     *
     * @return the brand name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns a human-readable string representation of this electronics product.
     *
     * @return a formatted string describing the electronics product
     */
    @Override
    public String toString() {
        return "Electronics Product\n" +
                super.toString() + "\n" +
                "Warranty Months: " + getWarrantMonths() + "\n" +
                "Brand: " + getBrand();
    }

    /**
     * Compares this electronics product to another object for equality.
     *
     * <p>
     * Two {@code ElectronicsProduct} instances are considered equal if their base
     * {@link Product} properties are equal and they have the same brand.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
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
