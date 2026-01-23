/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Concrete Builder for {@link BookProduct}.
 *
 * <p>
 * Implements the <b>Builder Pattern</b> for book products.
 * Creation is intended to be performed only by {@link ProductFactory}.
 * </p>
 */
public final class BookProductBuilder
        extends ProductBuilder<BookProduct, BookProductBuilder> {

    private String author;
    private int pages;

    @Override
    protected BookProductBuilder self() {
        return this;
    }

    public BookProductBuilder withAuthor(String author) {
        this.author = author;
        return this;
    }

    public BookProductBuilder withPages(int pages) {
        this.pages = pages;
        return this;
    }

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
