/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Builder for creating {@link ElectronicsProduct} instances.
 *
 * <p>
 * This class is a concrete builder in the Builder design pattern hierarchy.
 * It extends {@link ProductBuilder} and adds electronics-specific configuration,
 * such as warranty duration and brand.
 * </p>
 *
 * <p>
 * Intended to be used internally by {@link ProductFactory}.
 * </p>
 */
public final class ElectronicsProductBuilder
        extends ProductBuilder<ElectronicsProduct, ElectronicsProductBuilder> {

    /** Warranty duration in months. */
    private int warrantyMonths;

    /** Brand name of the electronics product. */
    private String brand;

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
    protected ElectronicsProductBuilder self() {
        return this;
    }

    /**
     * Sets the warranty duration for the electronics product.
     *
     * @param warrantyMonths warranty duration in months
     * @return this builder instance for method chaining
     */
    public ElectronicsProductBuilder withWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }

    /**
     * Sets the brand name for the electronics product.
     *
     * @param brand the brand name
     * @return this builder instance for method chaining
     */
    public ElectronicsProductBuilder withBrand(String brand) {
        this.brand = brand;
        return this;
    }

    /**
     * Builds and returns an {@link ElectronicsProduct} using the collected parameters.
     *
     * @return a newly created {@link ElectronicsProduct}
     */
    @Override
    public ElectronicsProduct build() {
        return new ElectronicsProduct(
                getName(),
                getPrice(),
                getStock(),
                getDescription(),
                getCategory(),
                getColor(),
                getImagePath(),
                warrantyMonths,
                brand
        );
    }
}
