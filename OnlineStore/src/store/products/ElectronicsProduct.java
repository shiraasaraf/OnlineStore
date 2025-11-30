package store.products;

import java.awt.*;

public class ElectronicsProduct extends Product{

    private int warrantMonths;
    private String brand;

    public ElectronicsProduct(String name, double price, int stock, String description, Category category,
                              Color color, int warrantMonths, String brand){
        super(name, price, stock, description, category, color);
       if (warrantMonths > 0) this.warrantMonths = warrantMonths;
       else this.warrantMonths = (-1)*warrantMonths;
       this.brand = brand;

    }
}
