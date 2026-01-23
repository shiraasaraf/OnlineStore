/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;
import java.util.Objects;

public class ElectronicsProduct extends Product {

    private int warrantMonths;
    private String brand;

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

    public int getWarrantMonths() {
        return warrantMonths;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public String toString() {
        return "Electronics Product\n" +
                super.toString() + "\n" +
                "Warranty Months: " + getWarrantMonths() + "\n" +
                "Brand: " + getBrand();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElectronicsProduct)) return false;
        if (!super.equals(o)) return false;

        ElectronicsProduct other = (ElectronicsProduct) o;
        return Objects.equals(this.brand, other.brand);
    }
}
