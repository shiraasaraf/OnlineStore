/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents a book product.
 * <p>
 * Extends {@link Product} with book-specific fields (author and page count).
 * </p>
 */
public class BookProduct extends Product {

    /** Book author. */
    private String author;

    /** Number of pages (positive). */
    private int pages;

    /**
     * Constructs a new book product.
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock
     * @param description product description
     * @param category    product category
     * @param color       product color
     * @param imagePath   relative image path
     * @param author      book author
     * @param pages       number of pages
     */
    public BookProduct(String name, double price, int stock, String description, Category category,
                       Color color, String imagePath, String author, int pages) {

        super(name, price, stock, description, category, color, imagePath);

        this.author = "Unknown author";
        this.pages = 1;

        if (author != null && !author.trim().isEmpty()) {
            this.author = author;
        }

        if (pages > 0) {
            this.pages = pages;
        }
    }

    /**
     * Returns the author name.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the number of pages.
     *
     * @return page count
     */
    public int getPages() {
        return pages;
    }

    /**
     * Returns a string representation of this book product.
     *
     * @return book product details
     */
    @Override
    public String toString() {
        return "Book Product\n" +
                super.toString() + "\n" +
                "Author: " + getAuthor() + "\n" +
                "Pages: " + getPages();
    }

    /**
     * Compares this book product to another object.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookProduct)) return false;
        if (!super.equals(o)) return false;

        BookProduct other = (BookProduct) o;
        return Objects.equals(this.author, other.author);
    }
}
