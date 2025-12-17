package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StoreWindow extends JFrame {

    private JPanel catalogPanel;
    private StoreController controller;
    private ProductDetailsPanel detailsPanel;
    private CartPanel cartPanel;


    public StoreWindow (StoreController storeController) {

        this.controller = storeController;
        cartPanel = new CartPanel();

        setTitle("Online Store");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new JLabel("Product Catalog"),BorderLayout.NORTH);


        catalogPanel = new JPanel();
        catalogPanel.setLayout(new GridLayout(0, 3, 10, 10));

        add(new JScrollPane(catalogPanel), BorderLayout.CENTER);
        add(cartPanel, BorderLayout.EAST);

        loadProductCatalog();
    }


    private void loadProductCatalog() {
        catalogPanel.removeAll();

        for (Product p : controller.getAvailableProducts()) {
            ProductPanel panel = new ProductPanel(p);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showProductDetails(p);
                }
            });

            catalogPanel.add(panel);
        }

        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    private void showProductDetails(Product product) {

        if (detailsPanel != null){
            remove(detailsPanel);
        }
        detailsPanel = new ProductDetailsPanel(product);


        detailsPanel.addAddToCartListener(e -> {
            boolean success = controller.addToCart(product, 1);

            if (success) {
                //TODO cartPanel.updateCart(controller.getCartProducts());
                JOptionPane.showMessageDialog(
                        this,
                        "Product added to the cart",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not add product to cart",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });



        add(detailsPanel, BorderLayout.EAST);
        revalidate();
        repaint();

    }


    //יתכן ונוסיף בהמשך מחלקות לview:
    //ProductCardView (כרטיס מוצר בגריד)
    //
    //CatalogPanel (הגריד/הרשימה של המוצרים)
    //
    //CartItemRowView (שורה/כרטיס של פריט בעגלה)


}
