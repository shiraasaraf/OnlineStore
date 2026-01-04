/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.app;

import store.engine.StoreEngine;
import store.gui.view.LauncherWindow;
import store.io.OrderHistoryIO;
import store.io.ProductCatalogIO;
import store.products.Product;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Application entry point.
 * <p>
 * Initializes the shared {@link StoreEngine}, loads a default product catalog
 * (if available), loads order history, and launches the {@link LauncherWindow}.
 * </p>
 */
public class Main {

    /** Default catalog CSV file name. */
    private static final String DEFAULT_CATALOG_FILE = "products_catalog.csv";

    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StoreEngine engine = StoreEngine.getInstance();

            loadDefaultCatalog(engine);
            loadOrderHistory(engine);

            LauncherWindow launcher = new LauncherWindow(engine);
            launcher.setVisible(true);
        });
    }

    /**
     * Loads products from the default catalog CSV file into the engine if the file exists.
     *
     * @param engine shared store engine
     */
    private static void loadDefaultCatalog(StoreEngine engine) {
        File file = new File(DEFAULT_CATALOG_FILE);
        if (!file.exists() || !file.isFile()) {
            return;
        }

        try {
            List<Product> loaded = ProductCatalogIO.loadProductsFromFile(file);
            for (Product p : loaded) {
                engine.addProduct(p);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads existing orders from the history file into the engine.
     *
     * @param engine shared store engine
     */
    private static void loadOrderHistory(StoreEngine engine) {
        engine.addLoadedOrders(OrderHistoryIO.loadOrders(engine));
    }
}
