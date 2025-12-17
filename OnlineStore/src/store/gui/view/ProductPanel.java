package store.gui.view;

import javax.swing.*;
import store.products.Product;

import java.awt.*;


public class ProductPanel extends JPanel {

    private Product product;

    public ProductPanel(Product product) {
        this.product = product;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel nameLabel = new JLabel(product.getDisplayName(), JLabel.CENTER);
        JLabel priceLabel = new JLabel("₪" + product.getPrice(), JLabel.CENTER);

        add(nameLabel, BorderLayout.CENTER);
        add(priceLabel, BorderLayout.SOUTH);

        setToolTipText(product.toString());

    }

    public Product getProduct() {
        return product;
    }

}
