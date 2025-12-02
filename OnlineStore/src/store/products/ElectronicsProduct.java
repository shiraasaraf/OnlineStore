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


    @Override
    public String toString() {
        final String BLUE = "\u001B[34m";
        final String RESET = "\u001B[0m";

        return BLUE + "A Clothing Product " + RESET + super.toString() + ", WarrantMonths " + warrantMonths + ",Brand " + brand;
    }

    public String getBrand() {
        return brand;
    }
    public int getWarrantMonths() {
        return warrantMonths;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o instanceof ElectronicsProduct) {
            ElectronicsProduct electronicsProduct = (ElectronicsProduct) o;
            return super.equals(electronicsProduct) && brand.equals(electronicsProduct.getBrand())
                    && warrantMonths == electronicsProduct.getWarrantMonths();
        }
        return false;
    }
}
