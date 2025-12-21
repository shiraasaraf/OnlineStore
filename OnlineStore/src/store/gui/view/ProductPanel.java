package store.gui.view;

import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import  java.net.URL;


public class ProductPanel extends JPanel {

    private final Product product;
    private final JLabel imageLabel = new JLabel();

    public ProductPanel(Product product) {
        this.product = product;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setText("");
        imageLabel.setPreferredSize(new Dimension(140, 100));
        add(imageLabel, BorderLayout.NORTH);

        JLabel nameLabel = new JLabel(product.getDisplayName(), JLabel.CENTER);
        JLabel priceLabel = new JLabel("$" + product.getPrice(), JLabel.CENTER);

        add(nameLabel, BorderLayout.CENTER);
        add(priceLabel, BorderLayout.SOUTH);

        setToolTipText(product.toString());

        loadImage();

    }

    private void loadImage() {
        String path = product.getImagePath(); // צריך להיות קיים ב-Product
        if (path == null || path.isBlank()) {
            imageLabel.setText("No Image");
            return;
        }

        // אם path הוא כמו: "images/phone.png" תחת resources
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) {
            imageLabel.setText("Image not found");
            return;
        }

        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
    }


    public Product getProduct() {
        return product;
    }

}
