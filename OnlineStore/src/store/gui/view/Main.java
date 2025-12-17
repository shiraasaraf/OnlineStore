package store.gui.view;

import java.awt.*;
import java.util.List;
import store.engine.*;
import store.core.*;
import store.gui.controller.*;
import store.products.BookProduct;
import store.products.Category;
import store.products.ElectronicsProduct;

public class Main {
    public static void main(String[] args) {



        StoreEngine engine = StoreEngine.getInstance();

        engine.addProduct(
                new BookProduct(
                        "Clean Code",
                        120.0,
                        10,
                        "A book about writing clean code",
                        Category.BOOKS,
                        Color.WHITE,
                        "Robert C. Martin",
                        450
                )
        );

        engine.addProduct(
                new ElectronicsProduct(
                        "Laptop",
                        3500.0,
                        5,
                        "Powerful laptop",
                        Category.ELECTRONICS,
                        Color.BLACK,
                        24,
                        "Dell"
                )
        );

        Customer customer = new Customer("shira", "mail@test.com");

        StoreController controller = new StoreController(engine, customer);
        StoreWindow window = new StoreWindow(controller);

        window.setVisible(true);
    }
}
