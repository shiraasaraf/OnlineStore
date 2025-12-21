package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.io.File;
import java.io.IOException;

public class StoreWindow extends JFrame {

    private final JPanel catalogPanel;
    private final StoreController controller;

    private final ProductDetailsPanel detailsPanel;
    private final CartPanel cartPanel;

    private final JButton loadButton;
    private final JButton saveButton;

    private final JButton manageCatalogButton;


    public StoreWindow(StoreController storeController) {

        this.controller = storeController;

        setTitle("Online Store");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        //Top bar: title + IO buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(new JLabel("Product Catalog"), BorderLayout.WEST);

        JPanel ioButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loadButton = new JButton("Loading products from a file");
        saveButton = new JButton("Saving products to a file");
        manageCatalogButton = new JButton("Manage catalog");

        manageCatalogButton.setEnabled(controller.canManage());

        // כיבוי במצב לקוח (רק Manager יכול לטעון/לשמור)
        loadButton.setEnabled(controller.canManage());
        saveButton.setEnabled(controller.canManage());

        // IO buttons actions: open file chooser and delegate to controller
        loadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                try {
                    controller.loadProductsFromFile(selectedFile);
                    // refresh catalog after loading
                    setCatalogProducts(controller.getAvailableProducts());
                    controller.saveCatalogToDefaultFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to load products from file.",
                            "IO Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                try {
                    controller.saveProductsToFile(selectedFile);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to save products to file.",
                            "IO Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        ioButtons.add(loadButton);
        ioButtons.add(saveButton);
        ioButtons.add(manageCatalogButton);

        manageCatalogButton.addActionListener(e -> {
            CatalogManagementWindow dialog = new CatalogManagementWindow(this, controller);
            dialog.setVisible(true);
            setCatalogProducts(controller.getAvailableProducts());
        });


        topBar.add(ioButtons, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        //Catalog Center
        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);

        //Right side: details + cart (must be 1 component in east)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        detailsPanel = new ProductDetailsPanel(null);
        cartPanel = new CartPanel();

        rightPanel.add(detailsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(cartPanel);

        add(rightPanel, BorderLayout.EAST);

        // connect "Add to cart" button from details panel
        detailsPanel.addAddToCartListener(e -> {
            Product p = detailsPanel.getProduct();
            int quantity = detailsPanel.getSelectedQuantity();

            boolean success = controller.addToCart(p, quantity);

            if (success) {
                cartPanel.setItems(controller.getItems());
                detailsPanel.showAddedFeedback(); //פידבק שנעלם אחרי שניה
                detailsPanel.setProduct(p); // לרענון stock אם ירד
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "could not add product to cart",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        //initial load for now
        setCatalogProducts(controller.getAvailableProducts());
    }

    public void setCatalogProducts(List<Product> products) {
        catalogPanel.removeAll();

        for (Product p : products) {
            ProductPanel panel = new ProductPanel(p);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    detailsPanel.setProduct(p);
                }
            });

            catalogPanel.add(panel);
        }

        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    //Hooks for IO buttons (optional – if רוצים לחבר מאזורים אחרים)
    public void addLoadProductsListener(ActionListener l) {
        loadButton.addActionListener(l);
    }

    public void addSaveProductsListener(ActionListener l) {
        saveButton.addActionListener(l);
    }
}
