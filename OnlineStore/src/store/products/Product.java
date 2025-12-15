/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;
import store.core.StoreEntity;
import store.core.Persistable;
import java.awt.Color;



/**
 * Abstract base class representing a generic product in the store.
 * Provides common fields such as name, price, stock, description, category
 * and color, and implements interfaces for display, pricing, stock management
 * and persistence.
 */
public abstract class Product implements StoreEntity, PricedItem, StockManageable, Persistable   {

    //data members:
    private String name;
    private double price;
    private int stock;
    private String description;
    private Category category;
    private Color color;

    /**
     * Constructs a new Product and applies validation to all parameters.
     * Invalid or null values are replaced with default values so the product
     * is always created in a valid state.
     *
     * @param name        product name (default used if null or empty)
     * @param price       product price (default used if not positive)
     * @param stock       initial stock (default used if negative)
     * @param description product description (empty string used if null)
     * @param category    product category (default used if null)
     * @param color       product color (default used if null)
     */
    public Product(String name, double price, int stock, String description,
                   Category category, Color color) {

        //default values
        this.name = "Unknown product";
        this.price = 0.1;
        this.stock = 0;
        this.description = "";
        this.category = Category.BOOKS; //Randomly selected because it must be from Category enum
        this.color = Color.BLACK; //Randomly selected because it must be from Color class

        // name – only if non-null and not blank
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
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
    setPrice(price);                // if invalid, keeps default price
    setStock(stock);                // if invalid, keeps default stock
    setDescription(description);    // if invalid, keeps default description
    }

    //----------------------------------------------------------------------------------------------------

    /**
     * Returns the product name.
     *
     * @return the product name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the product description.
     *
     * @return the product description (never null)
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the product category.
     *
     * @return the product category
     */
    public Category getCategory() {
        return this.category;
    }

    /**
     * Returns the product color.
     *
     * @return the product color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the initial stock value.
     * Used only inside the constructor.
     *
     * @param stock stock value to set
     * @return true if stock is valid and assigned, false otherwise
     */
    private boolean setStock(int stock) {
        if (stock < 0) {
            return false;
        }
        this.stock = stock;
        return true;
    }

    /**
     * Sets a new description if the value is not null.
     *
     * @param description new description
     * @return true if description was assigned, false otherwise
     */
    protected boolean setDescription(String description) {
        if (description == null) {
            return false;
        }
        this.description = description;
        return true;
    }

    //-------------------------------------------------------------------------------------

    //implementation of interfaces:

    //Persistable interface
    /**
     * Saves this product to a file.
     * Currently unimplemented and handled in future assignments.
     *
     * @param path path of the file to save into
     */
    @Override
    public void saveToFile(String path) {} //Temporarily implemented empty

    //interface StoreEntity

    /**
     * Returns a short display name for UI purposes.
     *
     * @return a display-friendly name
     */
    @Override
    public String getDisplayName(){
        return getName();
    }

    /**
     * Returns a detailed description for UI display,
     * including name, price, category, description and color.
     *
     * @return multi-line detailed product information
     */
    @Override
    public String getDisplayDetails() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Description: " + getDescription() + "\n" +
                "Color: " + getColor();
    }

    //-------------------------------------------------------------------------------------------

    //interface PricedItem

    /**
     * Returns the product price.
     *
     * @return the price of this product
     */
    @Override
    public double getPrice(){
        return price;
    }

    /**
     * Sets a new product price if it is positive.
     *
     * @param price new price value
     * @return true if assigned successfully, false otherwise
     */
    @Override
    public boolean setPrice(double price) {
        if (price <= 0) {
            return false;
        }
        this.price = price;
        return true;
    }

    //--------------------------------------------------------------------------------------------

    //interface StockManageable

    /**
     * Returns the current stock.
     *
     * @return the stock amount
     */
    @Override
    public int getStock() {
        return stock;
    }

    //set the stock through these 2 methods:

    /**
     * Increases stock by a positive amount.
     *
     * @param amount amount to increase
     * @return true if updated successfully, false otherwise
     */
    @Override
    public boolean increaseStock(int amount) {
        if (amount <= 0)
                return false;
        stock  = stock + amount;
        return true;
    }

    /**
     * Decreases stock by a positive amount without going negative.
     *
     * @param amount amount to decrease
     * @return true if updated successfully, false otherwise
     */
    @Override
    public boolean decreaseStock(int amount) {
        if (amount <= 0)
            return false;
        else if ((stock - amount) < 0)
            return false;
        stock = stock - amount;
        return true;
    }

    //----------------------------------------------------------------------------------------------

    //overrides must

    /**
     * Returns a basic multi-line string describing the product.
     *
     * @return product information string
     */
    @Override
    public String toString() {
        return "Name: " + getName() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Stock: " + getStock() ;
    }

    /**
     * Checks whether this product is equal to another object.
     *
     * Two Product objects are considered equal if:
     * 1. They are from the exact same class (same getClass()).
     * 2. They have the same name.
     * 3. They belong to the same category.
     *
     * This method compares only the fields defined in Product.
     * Subclass-specific fields are not included here.
     *
     * @param o the object to compare with this product
     * @return true if both products have the same class, name, and category;
     *         false otherwise
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;  //if it's the same object

        if (o == null || getClass() != o.getClass()){ //check specific class belonging (not general instance)
            return false;
        }

        Product other = (Product) o; //casting to Product

        return java.util.Objects.equals(this.name, other.name) && //compare
                this.category == other.category;
    }


}


