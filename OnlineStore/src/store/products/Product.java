package store.products;
import store.core.StoreEntity;
import store.core.Persistable;
import java.awt.Color;

public abstract class  Product implements StoreEntity, PricedItem, StockManageable, Persistable   {

    //data members:
    private String name;
    private double price;
    private int stock;
    private String description;
    private Category category;
    private Color color;

    //c'tor
    public Product(String name, double price, int stock, String description,
                   Category category, Color color) {
        this.name = name;
        setPrice(price);
        setStock(stock);
        this.description = description;
        this.category = category;
        this.color = color;
    }


    //stock setter
    public boolean setStock(int stock) {
        if (stock < 0) {
            return false;   // ערך לא תקין → לא משנים
        }
        this.stock = stock;
        return true;        // הצליח
    }


    //overrides must
    @Override
    public String toString() {
        return "Name: " + name + ", Price: " + price + ", Category: " + category + ", Stock: " + stock;
    }

    @Override
    public boolean equals(Object o) {
        //if it's the same object
        if (this == o) return true;

        //check the instance
        if (!(o instanceof Product)) return false;

        //casting to Product
        Product other = (Product) o;

        //compare
        return this.name.equals(other.name) &&
                this.category == other.category;
    }

    //implementation of interfaces-
    // todo: check what methods to remove here and move to specific sub-class
    // todo: methods implementation is not correct. have to write them from first, check if public

    //Persistable interface
    public void saveToFile(String path) {}

    //interface StoreEntity
    public String getDisplayName(){
        return name;
    }
    public String getDisplayDetails(){
        return name;
    }

    //interface PricedItem
    public double getPrice(){
        return price;
    }

    //price setter
    public boolean setPrice(double price){
        if (price <= 0) {
            return false;
        }
        this.price = price;
        return true;
    }

    //interface StockManageable
    public int getStock() {
        return stock;
    }
    public boolean increaseStock(int amount){
        return true;
    }
    public boolean decreaseStock(int amount){
        return true;
    }

}


