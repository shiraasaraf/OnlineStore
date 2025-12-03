/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */


package store.products;

import java.awt.Color;

/**
 * Represents a book product in the store.
 * Extends {@link Product} by adding book-specific fields such as
 * author and number of pages.
 *
 * This class adds additional details relevant only to book products
 * while keeping all product validation and shared behavior in the
 * parent class Product.
 */
public class BookProduct extends Product {

    //data members
    private String author;
    private int pages;


    /**
     * Constructs a new BookProduct with validation.
     * Invalid or null parameters are replaced with default values.
     *
     * @param name        product name (default if null or empty)
     * @param price       product price (default if not positive)
     * @param stock       initial stock (default if negative)
     * @param description product description (empty string if null)
     * @param category    product category (default if null)
     * @param color       product color (default if null)
     * @param author      book author (default if null or empty)
     * @param pages       number of pages (must be positive, otherwise default)
     */
    public BookProduct(String name, double price, int stock, String description, Category category,
                       Color color, String author, int pages){

        super(name, price, stock, description, category, color);

        //default values
        this.author = "Unknown author";
        this.pages = 1;

        // author – only if not null and not blank
        if (author != null && !author.trim().isEmpty()) {
            this.author = author;
        }

        // pages – must be positive
        if (pages > 0) {
            this.pages = pages;
        }
    }

    //------------------------------------------------------------------------------------------

    /**
     * Returns the author of this book.
     *
     * @return the author's name
     */
    public String getAuthor() {
        return this.author;
    }


    /**
     * Returns the number of pages in this book.
     *
     * @return the page count
     */
    public int getPages() { return this.pages; }


    //----------------------------------------------------------------------------------------

    /**
     * Returns a multi-line string describing this book product,
     * including all product details and book-specific fields.
     *
     * @return string representation of this book
     */
    @Override
    public String toString() {
        return "Book Product\n" +
            super.toString() + "\n" +
                "Author: " + getAuthor() + "\n" +
                "Pages: " + getPages();
    }

    //equals Implemented in parent class

}
