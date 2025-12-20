package store.app;

import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.gui.controller.StoreController;
import store.gui.view.LoginWindow;
import store.gui.view.StoreWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StoreEngine engine = StoreEngine.getInstance();

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
}
