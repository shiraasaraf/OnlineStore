package store.products;

import java.awt.*;

public class ClothingProduct extends Product {

    private String size;

    public ClothingProduct(String name, double price, int stock, String description, Category category,
                           Color color, String size) {
        super(name, price, stock, description, category, color);
        this.size = size;

    }

    @Override
    public String toString() {
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        return RED + "A Clothing Product " + RESET + super.toString() + ", Size: " + size;
    }

    public String getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o instanceof ClothingProduct) {
            ClothingProduct clothingProduct = (ClothingProduct) o;
            return super.equals(clothingProduct) && size.equals(clothingProduct.getSize());
        }
        return false;
    }



}
