/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Concrete Builder for {@link ElectronicsProduct}.
 *
 * <p>
 * Implements the <b>Builder Design Pattern</b> for electronics products.
 * Used exclusively by {@link ProductFactory}.
 * </p>
 */
public final class ElectronicsProductBuilder
        extends ProductBuilder<ElectronicsProduct, ElectronicsProductBuilder> {

    private int warrantyMonths;
    private String brand;

    @Override
    protected ElectronicsProductBuilder self() {
        return this;
    }

    public ElectronicsProductBuilder withWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }

    public ElectronicsProductBuilder withBrand(String brand) {
        this.brand = brand;
        return this;
    }

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
