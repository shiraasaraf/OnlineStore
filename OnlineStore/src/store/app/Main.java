package store.app;

import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.gui.controller.StoreController;
import store.gui.view.LoginWindow;
import store.gui.view.StoreWindow;
import store.io.ProductCatalogIO;
import store.products.Product;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    private static final String DEFAULT_CATALOG_FILE = "products_catalog.csv";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StoreEngine engine = StoreEngine.getInstance();

            // Load default catalog if exists (best effort)
            File file = new File(DEFAULT_CATALOG_FILE);
            if (file.exists() && file.isFile()) {
                try {
                    List<Product> loaded = ProductCatalogIO.loadProductsFromFile(file);
                    for (Product p : loaded) {
                        engine.addProduct(p);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // אפשר גם JOptionPane אם את רוצה
                }
            }

            LoginWindow login = new LoginWindow();

            // Customer mode
            login.addCustomerListener(e -> {
                Customer customer = new Customer("Guest", "guest@example.com");
                Manager manager = null;

                StoreController controller = new StoreController(engine, customer, manager);
                StoreWindow window = new StoreWindow(controller);

                login.dispose();
                window.setVisible(true);
            });

            // Manager mode
            login.addManagerListener(e -> {
                Customer customer = new Customer("Guest", "guest@example.com");
                Manager manager = new Manager("Admin", "admin@example.com");

                StoreController controller = new StoreController(engine, customer, manager);
                StoreWindow window = new StoreWindow(controller);

                login.dispose();
                window.setVisible(true);
            });

            login.setVisible(true);
        });
    }
}
