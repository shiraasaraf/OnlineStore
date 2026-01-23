/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Builder for creating {@link ClothingProduct} instances.
 *
 * <p>
 * This class is a concrete builder in the Builder design pattern hierarchy.
 * It extends {@link ProductBuilder} and adds clothing-specific configuration,
 * such as size.
 * </p>
 *
 * <p>
 * Intended to be used internally by {@link ProductFactory}.
 * </p>
 */
public final class ClothingProductBuilder
        extends ProductBuilder<ClothingProduct, ClothingProductBuilder> {

    /** Size descriptor of the clothing item. */
    private String size;

    /**
     * Returns this builder instance.
     *
     * <p>
     * Used by the self-referential generic pattern to enable fluent method chaining.
     * </p>
     *
     * @return this builder instance
     */
    @Override
    protected ClothingProductBuilder self() {
        return this;
    }

    /**
     * Sets the size of the clothing product.
     *
     * @param size the size descriptor
     * @return this builder instance for method chaining
     */
    public ClothingProductBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    /**
     * Builds and returns a {@link ClothingProduct} using the collected parameters.
     *
     * @return a newly created {@link ClothingProduct}
     */
    @Override
    public ClothingProduct build() {
        return new ClothingProduct(
                getName(),
                getPrice(),
                getStock(),
                getDescription(),
                getCategory(),
                getColor(),
                getImagePath(),
                size
        );
    }
}
