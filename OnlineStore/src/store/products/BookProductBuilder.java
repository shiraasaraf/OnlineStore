/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Builder implementation for creating {@link BookProduct} instances.
 *
 * <p>
 * This class is a concrete builder in the <b>Builder design pattern</b> hierarchy.
 * It extends {@link ProductBuilder} and adds book-specific attributes such as
 * author and number of pages.
 * </p>
 *
 * <p>
 * The builder is intended to be used internally by {@link ProductFactory} and
 * not directly by client code.
 * </p>
 */
public final class BookProductBuilder
        extends ProductBuilder<BookProduct, BookProductBuilder> {

    /** Author of the book. */
    private String author;

    /** Number of pages in the book. */
    private int pages;

    /**
     * Returns this builder instance.
     *
     * <p>
     * Used by the self-referential generic pattern to enable fluent method chaining
     * in subclasses of {@link ProductBuilder}.
     * </p>
     *
     * @return this builder instance
     */
    @Override
    protected BookProductBuilder self() {
        return this;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the author name
     * @return this builder instance for method chaining
     */
    public BookProductBuilder withAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Sets the number of pages of the book.
     *
     * @param pages number of pages
     * @return this builder instance for method chaining
     */
    public BookProductBuilder withPages(int pages) {
        this.pages = pages;
        return this;
    }

    /**
     * Builds and returns a {@link BookProduct} instance using the collected parameters.
     *
     * @return a newly created {@link BookProduct}
     */
    @Override
    public BookProduct build() {
        return new BookProduct(
                getName(),
                getPrice(),
                getStock(),
                getDescription(),
                getCategory(),
                getColor(),
                getImagePath(),
                author,
                pages
        );
    }
}
