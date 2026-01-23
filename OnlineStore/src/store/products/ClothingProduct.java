/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;
import java.util.Objects;

public class ClothingProduct extends Product {

    private String size;

    ClothingProduct(String name, double price, int stock, String description,
                    Category category, Color color, String imagePath, String size) {

        super(name, price, stock, description, category, color, imagePath);

        this.size = "Unknown size";

        if (size != null && !size.trim().isEmpty()) {
            this.size = size.trim();
        }
    }

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Clothing Product\n" +
                super.toString() + "\n" +
                "Size: " + getSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClothingProduct)) return false;
        if (!super.equals(o)) return false;

        ClothingProduct other = (ClothingProduct) o;
        return Objects.equals(this.size, other.size);
    }
}
