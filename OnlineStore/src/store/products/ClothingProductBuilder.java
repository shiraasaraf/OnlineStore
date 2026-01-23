/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

/**
 * Concrete Builder for {@link ClothingProduct}.
 *
 * <p>
 * Implements the <b>Builder Design Pattern</b> for clothing products.
 * Used exclusively by {@link ProductFactory}.
 * </p>
 */
public final class ClothingProductBuilder
        extends ProductBuilder<ClothingProduct, ClothingProductBuilder> {

    private String size;

    @Override
    protected ClothingProductBuilder self() {
        return this;
    }

    public ClothingProductBuilder withSize(String size) {
        this.size = size;
        return this;
    }

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
