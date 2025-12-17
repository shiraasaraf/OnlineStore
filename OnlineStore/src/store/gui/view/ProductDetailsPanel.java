package store.gui.view;

import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ProductDetailsPanel extends JPanel {

    private Product product;
    private JButton addToCartButton;

    public ProductDetailsPanel(Product product) {
        this.product = product;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Product Details"));

        add(new JLabel("Name: " + product.getDisplayName()));
        add(new JLabel("Price: ₪" + product.getPrice()));
        add(new JLabel("Stock: " + product.getStock()));

        JTextArea descriptionArea = new JTextArea(product.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);

        add(new JScrollPane(descriptionArea));

        addToCartButton = new JButton("Add to Cart");
        add(addToCartButton);
    }

    public void addAddToCartListener(ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }

    public Product getProduct() {
        return product;
    }

}
