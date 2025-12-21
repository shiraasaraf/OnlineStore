package store.app;

import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.gui.controller.StoreController;
import store.gui.view.LoginWindow;
import store.gui.view.StoreWindow;
import store.products.*;


import javax.swing.*;
import java.awt.*;


public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StoreEngine engine = StoreEngine.getInstance();

            engine.loadDefaultCatalogIfExists();

            LoginWindow login = new LoginWindow();

            // Customer mode
            login.addCustomerListener(e -> {
                Customer customer = new Customer("Guest", "guest@example.com");
                Manager manager = null; // אין הרשאות ניהול

                StoreController controller = new StoreController(engine, customer, manager);
                StoreWindow window = new StoreWindow(controller);

                login.dispose();
                window.setVisible(true);
            });

            // Manager mode
            login.addManagerListener(e -> {
                Customer customer = new Customer("Guest", "guest@example.com");
                Manager manager = new Manager("Admin", "admin@example.com"); // יש הרשאות ניהול

                StoreController controller = new StoreController(engine, customer, manager);
                StoreWindow window = new StoreWindow(controller);

                login.dispose();
                window.setVisible(true);
            });

            login.setVisible(true);
        });
    }


    private static void seedProducts(StoreEngine engine) {

        engine.addProduct(new ElectronicsProduct(
                "Laptop",
                3500.0,
                5,
                "14 inch laptop",
                Category.ELECTRONICS,
                Color.BLACK,
                24,
                "Dell"
        ));

        engine.addProduct(new ElectronicsProduct(
                "Phone",
                2500.0,
                8,
                "Android Phone",
                Category.ELECTRONICS,
                Color.GRAY,
                12,
                "Samsung"
        ));
    }


}



