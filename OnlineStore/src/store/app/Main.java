/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.app;

import store.engine.StoreEngine;
import store.gui.view.LauncherWindow;
import store.io.ProductCatalogIO;
import store.products.Product;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main application class.
 * Application entry point.
 * <p>
 * Initializes the store engine, loads a default product catalog if available,
 * and launches the main launcher window (which stays open).
 * </p>
 */
public class Main {

    /** Default catalog file name. */
    private static final String DEFAULT_CATALOG_FILE = "products_catalog.csv";

    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StoreEngine engine = StoreEngine.getInstance();

            // Load default catalog if exists
            File file = new File(DEFAULT_CATALOG_FILE);
            if (file.exists() && file.isFile()) {
                try {
                    List<Product> loaded = ProductCatalogIO.loadProductsFromFile(file);
                    for (Product p : loaded) {
                        engine.addProduct(p);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            // BONUS: load existing orders history
            engine.loadOrderHistoryFromFile();

            // Required in EX3: launcher stays open and can open multiple customer/admin windows
            LauncherWindow launcher = new LauncherWindow(engine);
            launcher.setVisible(true);

        });
    }
}
