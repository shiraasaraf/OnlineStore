/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents a book product in the store catalog.
 *
 * <p>
 * A {@code BookProduct} extends {@link Product} by adding book-specific attributes
 * such as author name and number of pages.
 * </p>
 *
 * <p>
 * Instances of this class are intended to be created via {@link ProductFactory}
 * and not directly by client code.
 * </p>
 */
public class BookProduct extends Product {

    /** The author of the book. */
    private String author;

    /** Number of pages in the book. */
    private int pages;

    /**
     * Constructs a new book product.
     *
     * <p>
     * If the provided author is {@code null} or blank, a default value is used.
     * If the number of pages is non-positive, a default value is used.
     * </p>
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock quantity
     * @param description textual description of the book
     * @param category    product category (expected to be {@link Category#BOOKS})
     * @param color       display color associated with the product
     * @param imagePath   path to the product image resource
     * @param author      author name
     * @param pages       number of pages
     */
    BookProduct(String name, double price, int stock, String description, Category category,
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
     * Returns the author of the book.
     *
     * @return the author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the number of pages in the book.
     *
     * @return the page count
     */
    public int getPages() {
        return pages;
    }

    /**
     * Returns a human-readable string representation of this book product.
     *
     * @return a formatted string describing the book product
     */
    @Override
    public String toString() {
        return "Book Product\n" +
                super.toString() + "\n" +
                "Author: " + getAuthor() + "\n" +
                "Pages: " + getPages();
    }

    /**
     * Compares this book product to another object for equality.
     *
     * <p>
     * Two {@code BookProduct} instances are considered equal if their base
     * {@link Product} properties are equal and they have the same author.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
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
