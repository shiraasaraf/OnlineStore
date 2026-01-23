/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.products;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for creating {@link Product} instances according to {@link Category}.
 *
 * <p>
 * This class combines the Factory pattern with Builder usage:
 * for each category, a category-specific builder is used to construct the concrete
 * product subtype (e.g., books, clothing, electronics).
 * </p>
 *
 * <p>
 * Creation is centralized through {@link #createProduct(BasicFields, ExtraFields)},
 * while convenient helper methods are provided for creating specific product types.
 * </p>
 */
public final class ProductFactory {

    /**
     * Prevents instantiation; this is a static utility/factory class.
     */
    private ProductFactory() {}

    /**
     * Functional interface used to create a product instance from a common basic fields block
     * and an optional extra fields block.
     */
    private interface Creator {
        /**
         * Creates a product instance using the given data.
         *
         * @param basic common product fields
         * @param extra category-specific fields
         * @return the created product
         */
        Product create(BasicFields basic, ExtraFields extra);
    }

    /**
     * Immutable container for common fields shared by all products.
     */
    public static final class BasicFields {
        /** Product name. */
        public final String name;

        /** Product price. */
        public final double price;

        /** Initial stock quantity. */
        public final int stock;

        /** Product description. */
        public final String description;

        /** Product category. */
        public final Category category;

        /** Product display color. */
        public final Color color;

        /** Relative path to the product image resource. */
        public final String imagePath;

        /**
         * Constructs a container of basic product fields.
         *
         * @param name        product name
         * @param price       product price
         * @param stock       initial stock quantity
         * @param description product description
         * @param category    product category
         * @param color       product display color
         * @param imagePath   relative image path in resources
         */
        public BasicFields(String name, double price, int stock, String description,
                           Category category, Color color, String imagePath) {
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.description = description;
            this.category = category;
            this.color = color;
            this.imagePath = imagePath;
        }
    }

    /**
     * Mutable container for category-specific fields.
     *
     * <p>
     * Only the relevant fields are expected to be populated according to the target category.
     * </p>
     */
    public static final class ExtraFields {
        /** Book author (books). */
        public String author;

        /** Book pages count (books). */
        public Integer pages;

        /** Clothing size (clothing). */
        public String size;

        /** Warranty duration in months (electronics). */
        public Integer warrantyMonths;

        /** Brand name (electronics). */
        public String brand;
    }

    /**
     * Registry mapping each category to its creation logic.
     */
    private static final Map<Category, Creator> CREATORS = new EnumMap<>(Category.class);

    static {
        CREATORS.put(Category.BOOKS, (basic, extra) -> new BookProductBuilder()
                .withName(basic.name)
                .withPrice(basic.price)
                .withStock(basic.stock)
                .withDescription(basic.description)
                .withCategory(basic.category)
                .withColor(basic.color)
                .withImagePath(basic.imagePath)
                .withAuthor(extra.author)
                .withPages(extra.pages == null ? 0 : extra.pages)
                .build());

        CREATORS.put(Category.CLOTHING, (basic, extra) -> new ClothingProductBuilder()
                .withName(basic.name)
                .withPrice(basic.price)
                .withStock(basic.stock)
                .withDescription(basic.description)
                .withCategory(basic.category)
                .withColor(basic.color)
                .withImagePath(basic.imagePath)
                .withSize(extra.size)
                .build());

        CREATORS.put(Category.ELECTRONICS, (basic, extra) -> new ElectronicsProductBuilder()
                .withName(basic.name)
                .withPrice(basic.price)
                .withStock(basic.stock)
                .withDescription(basic.description)
                .withCategory(basic.category)
                .withColor(basic.color)
                .withImagePath(basic.imagePath)
                .withWarrantyMonths(extra.warrantyMonths == null ? 0 : extra.warrantyMonths)
                .withBrand(extra.brand)
                .build());
    }

    /**
     * Creates a product instance for the given {@link Category}.
     *
     * <p>
     * The category is taken from {@code basic.category} and is used to select the relevant
     * builder via the internal registry.
     * </p>
     *
     * @param basic common product fields (must not be {@code null} and must include a non-null category)
     * @param extra category-specific fields (may be {@code null})
     * @return a concrete {@link Product} instance
     * @throws IllegalArgumentException if the category is missing or unsupported
     */
    public static Product createProduct(BasicFields basic, ExtraFields extra) {
        if (basic == null || basic.category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }

        Creator creator = CREATORS.get(basic.category);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported category: " + basic.category);
        }

        return creator.create(basic, extra == null ? new ExtraFields() : extra);
    }

    /**
     * Creates a product using default values for missing category-specific fields.
     *
     * <p>
     * Defaults depend on {@code basic.category}. The returned instance is created via
     * {@link #createProduct(BasicFields, ExtraFields)}.
     * </p>
     *
     * @param basic common product fields (must include a non-null category)
     * @return a concrete {@link Product} instance with default extra fields applied
     * @throws IllegalArgumentException if category is missing or unsupported
     */
    public static Product createProductWithDefaults(BasicFields basic) {
        ExtraFields extra = new ExtraFields();
        if (basic != null && basic.category != null) {
            switch (basic.category) {
                case BOOKS:
                    extra.author = "Unknown author";
                    extra.pages = 100;
                    break;
                case CLOTHING:
                    extra.size = "M";
                    break;
                case ELECTRONICS:
                default:
                    extra.warrantyMonths = 12;
                    extra.brand = "Generic Brand";
                    break;
            }
        }
        return createProduct(basic, extra);
    }

    /**
     * Convenience factory method for creating a {@link BookProduct}.
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock quantity
     * @param description product description
     * @param color       product display color
     * @param imagePath   relative image path in resources
     * @param author      book author
     * @param pages       number of pages
     * @return a {@link BookProduct} instance
     * @throws IllegalArgumentException if category creation fails
     */
    public static BookProduct createBook(String name, double price, int stock, String description,
                                         Color color, String imagePath, String author, int pages) {
        BasicFields basic = new BasicFields(name, price, stock, description, Category.BOOKS, color, imagePath);
        ExtraFields extra = new ExtraFields();
        extra.author = author;
        extra.pages = pages;
        return (BookProduct) createProduct(basic, extra);
    }

    /**
     * Convenience factory method for creating a {@link ClothingProduct}.
     *
     * @param name        product name
     * @param price       product price
     * @param stock       initial stock quantity
     * @param description product description
     * @param color       product display color
     * @param imagePath   relative image path in resources
     * @param size        clothing size
     * @return a {@link ClothingProduct} instance
     * @throws IllegalArgumentException if category creation fails
     */
    public static ClothingProduct createClothing(String name, double price, int stock, String description,
                                                 Color color, String imagePath, String size) {
        BasicFields basic = new BasicFields(name, price, stock, description, Category.CLOTHING, color, imagePath);
        ExtraFields extra = new ExtraFields();
        extra.size = size;
        return (ClothingProduct) createProduct(basic, extra);
    }

    /**
     * Convenience factory method for creating an {@link ElectronicsProduct}.
     *
     * @param name           product name
     * @param price          product price
     * @param stock          initial stock quantity
     * @param description    product description
     * @param color          product display color
     * @param imagePath      relative image path in resources
     * @param warrantyMonths warranty duration in months
     * @param brand          product brand
     * @return an {@link ElectronicsProduct} instance
     * @throws IllegalArgumentException if category creation fails
     */
    public static ElectronicsProduct createElectronics(String name, double price, int stock, String description,
                                                       Color color, String imagePath, int warrantyMonths, String brand) {
        BasicFields basic = new BasicFields(name, price, stock, description, Category.ELECTRONICS, color, imagePath);
        ExtraFields extra = new ExtraFields();
        extra.warrantyMonths = warrantyMonths;
        extra.brand = brand;
        return (ElectronicsProduct) createProduct(basic, extra);
    }
}
