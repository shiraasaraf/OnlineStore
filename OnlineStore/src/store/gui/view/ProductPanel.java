/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Panel that displays a product card in the catalog grid.
 * <p>
 * Shows the product image, name and price, and provides a tooltip with
 * product details.
 * </p>
 */
public class ProductPanel extends JPanel {

    /** Product represented by this panel. */
    private final Product product;

    /** Label used to display the product image. */
    private final JLabel imageLabel = new JLabel();

    /** Label used to display the product name. */
    private final JLabel nameLabel;

    /** Label used to display the product price. */
    private final JLabel priceLabel;

    /** Fixed card size. */
    private static final int CARD_W = 170;

    /** Fixed card size. */
    private static final int CARD_H = 220;

    /** Image display width. */
    private static final int IMG_W = 169;

    /** Image display height. */
    private static final int IMG_H = 219;

    /**
     * Constructs a new product panel for the given product.
     *
     * @param product product to display
     */
    public ProductPanel(Product product) {
        this.product = product;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(CARD_W, CARD_H));
        setBackground(Color.WHITE);
        setOpaque(true);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setPreferredSize(new Dimension(IMG_W, IMG_H));
        add(imageLabel, BorderLayout.CENTER);

        nameLabel = new JLabel(product.getDisplayName(), SwingConstants.CENTER);
        priceLabel = new JLabel("$" + product.getPrice(), SwingConstants.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Color.WHITE);

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 8, 6));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(priceLabel);

        add(infoPanel, BorderLayout.SOUTH);

        setToolTipText(product.toString());

        loadImage();
    }

    /**
     * Returns the product displayed by this panel.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Loads and scales the product image into the image label.
     */
    private void loadImage() {
        try {
            String path = product.getImagePath();

            if (path == null || path.isBlank()) {
                imageLabel.setIcon(null);
                imageLabel.setText("No Image");
                return;
            }

            URL url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                imageLabel.setIcon(null);
                imageLabel.setText("Image not found");
                return;
            }

            ImageIcon icon = new ImageIcon(url);
            Image scaled = icon.getImage().getScaledInstance(IMG_W, IMG_H, Image.SCALE_SMOOTH);

            imageLabel.setText("");
            imageLabel.setIcon(new ImageIcon(scaled));

        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image error");
        }
    }
}
