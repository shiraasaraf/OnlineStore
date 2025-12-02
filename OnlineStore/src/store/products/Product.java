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
    /**
     * Constructs a new Product with validation and default values.
     *
     * @param name          product name (must be non-null and non-empty to be used)
     * @param price         product price (must be positive)
     * @param stock         initial stock amount (must be non-negative)
     * @param description   product description (may be null)
     * @param category      product category (must be non-null to be used)
     * @param color         product color (may be null)
     */
    public Product(String name, double price, int stock, String description,
                   Category category, Color color) {
        //default values
        this.name = "Unknown product";
        this.price = 0.1;
        this.stock = 0;
        this.description = "";
        this.category = Category.BOOKS; //must be something from enum. not null
        this.color = Color.BLACK; ////must be something from Color. not null

        // name – only if non-null and not blank
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }

        // description – allow empty, but not null
        if (description != null) {
            this.description = description;
        }

        // category – only if non-null
        if (category != null) {
            this.category = category;
        }

        // color – only if non-null
        if (color != null) {
            this.color = color;
        }

        // use setters so validation is centralized
        setPrice(price);   // if invalid, keeps default price
        setStock(stock);   // if invalid, keeps default stock
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
        return "Name: " + name + "\n" +
                "Price: " + price + "\n" +
                "Category: " + category + "\n" +
                "Stock: " + stock;
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

    //Persistable interface
    public void saveToFile(String path) {}

    //interface StoreEntity
    public String getDisplayName(){
        return this.name;
    }
    public String getDisplayDetails(){
        return "Name: " + name + "\n" +
                "Price: " + price + "\n" +
                "Category: " + category + "\n" +
                "Description: " + description + "\n" +
                "Color: " + color;
    }

    // todo: methods implementation is not correct. have to write them from first, check if public
    //todo: add getters setters javadoc
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


