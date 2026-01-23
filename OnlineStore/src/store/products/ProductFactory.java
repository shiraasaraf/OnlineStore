/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.products;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

public final class ProductFactory {

    private ProductFactory() {}

    private interface Creator {
        Product create(BasicFields basic, ExtraFields extra);
    }

    public static final class BasicFields {
        public final String name;
        public final double price;
        public final int stock;
        public final String description;
        public final Category category;
        public final Color color;
        public final String imagePath;

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

    public static final class ExtraFields {
        public String author;
        public Integer pages;
        public String size;
        public Integer warrantyMonths;
        public String brand;
    }

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

    public static BookProduct createBook(String name, double price, int stock, String description,
                                         Color color, String imagePath, String author, int pages) {
        BasicFields basic = new BasicFields(name, price, stock, description, Category.BOOKS, color, imagePath);
        ExtraFields extra = new ExtraFields();
        extra.author = author;
        extra.pages = pages;
        return (BookProduct) createProduct(basic, extra);
    }

    public static ClothingProduct createClothing(String name, double price, int stock, String description,
                                                 Color color, String imagePath, String size) {
        BasicFields basic = new BasicFields(name, price, stock, description, Category.CLOTHING, color, imagePath);
        ExtraFields extra = new ExtraFields();
        extra.size = size;
        return (ClothingProduct) createProduct(basic, extra);
    }

    public static ElectronicsProduct createElectronics(String name, double price, int stock, String description,
                                                       Color color, String imagePath, int warrantyMonths, String brand) {
        BasicFields basic = new BasicFields(name, price, stock, description, Category.ELECTRONICS, color, imagePath);
        ExtraFields extra = new ExtraFields();
        extra.warrantyMonths = warrantyMonths;
        extra.brand = brand;
        return (ElectronicsProduct) createProduct(basic, extra);
    }
}
