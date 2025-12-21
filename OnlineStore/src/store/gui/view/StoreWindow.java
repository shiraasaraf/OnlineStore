package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class StoreWindow extends JFrame {

    private final JPanel catalogPanel;
    private final StoreController controller;

    private final ProductDetailsPanel detailsPanel;
    private final CartPanel cartPanel;

    private final JButton loadButton;
    private final JButton saveButton;
    private final JButton manageCatalogButton;

    private final JButton historyButton;


    public StoreWindow(StoreController storeController) {

        this.controller = storeController;

        setTitle("Online Store");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top bar: title + buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(new JLabel("Product Catalog"), BorderLayout.WEST);

        JPanel ioButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loadButton = new JButton("Loading products from a file");
        saveButton = new JButton("Saving products to a file");
        manageCatalogButton = new JButton("Manage catalog");
        historyButton = new JButton("Order History");


        // Permissions: only Manager can manage / load / save
        boolean isManager = controller.canManage();
        manageCatalogButton.setEnabled(isManager);
        loadButton.setEnabled(isManager);
        saveButton.setEnabled(isManager);
        historyButton.setEnabled(controller.canManage());


        // ---- Load button ----
        loadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File selectedFile = chooser.getSelectedFile();
            try {
                controller.loadProductsFromFile(selectedFile);

                // refresh catalog after loading
                setCatalogProducts(controller.getAvailableProducts());

                JOptionPane.showMessageDialog(
                        this,
                        "Products loaded successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to load products from file:\n" + ex.getMessage(),
                        "IO Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // ---- Save button ----
        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File selectedFile = chooser.getSelectedFile();

            try {
                controller.saveProductsToFile(selectedFile);

                JOptionPane.showMessageDialog(
                        this,
                        "Products saved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save products to file:\n" + ex.getMessage(),
                        "IO Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // ---- Manage catalog ----
        manageCatalogButton.addActionListener(e -> {
            CatalogManagementWindow dialog = new CatalogManagementWindow(this, controller);
            dialog.setVisible(true);

            // refresh after manager changes catalog
            setCatalogProducts(controller.getAvailableProducts());
        });

        historyButton.addActionListener(e -> {
            OrderHistoryWindow dialog = new OrderHistoryWindow(this, controller);
            dialog.setVisible(true);
        });


        ioButtons.add(loadButton);
        ioButtons.add(saveButton);
        ioButtons.add(manageCatalogButton);
        ioButtons.add(historyButton);


        topBar.add(ioButtons, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Catalog Center
        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);

        // Right side: details + cart
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        detailsPanel = new ProductDetailsPanel(null);
        cartPanel = new CartPanel();

        cartPanel.addRemoveItemListener(ev -> {
            JButton btn = (JButton) ev.getSource();
            Product p = (Product) btn.getClientProperty("product");
            if (p == null) return;

            boolean removed = controller.removeFromCart(p); // נוסיף מתודה כזו
            if (!removed) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to remove item from cart",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            cartPanel.setItems(controller.getItems());
            setCatalogProducts(controller.getAvailableProducts());
            detailsPanel.setProduct(detailsPanel.getProduct());
        });



        rightPanel.add(detailsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(cartPanel);

        add(rightPanel, BorderLayout.EAST);

        // connect "Add to cart" button from details panel
        detailsPanel.addAddToCartListener(e -> {
            Product p = detailsPanel.getProduct();
            if (p == null) return;

            int quantity = detailsPanel.getSelectedQuantity();
            boolean success = controller.addToCart(p, quantity);

            if (success) {
                cartPanel.setItems(controller.getItems());
                detailsPanel.showAddedFeedback(); // feedback disappears after 1 second
                detailsPanel.setProduct(p);       // refresh stock view
                setCatalogProducts(controller.getAvailableProducts()); // optional: refresh catalog grid
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not add product to cart",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // initial catalog
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
}
