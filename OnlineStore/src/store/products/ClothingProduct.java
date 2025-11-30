package store.products;

import java.awt.*;

public class ClothingProduct extends Product {

    private String size;

    public ClothingProduct(String name, double price, int stock, String description, Category category,
                           Color color, String size) {
        super(name, price, stock, description, category, color);
        this.size = size;

    }

    //TODO - toString



}
