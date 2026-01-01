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
 * Also indicates "Out of stock" when stock is 0.
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

    /** "Out of stock" badge (shown only when stock==0). */
    private final JLabel outOfStockBadge = new JLabel("OUT OF STOCK");

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

        // Center: image with optional "OUT OF STOCK" badge on top
        JPanel imageContainer = new JPanel(null);
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setOpaque(true);
        imageContainer.setPreferredSize(new Dimension(IMG_W, IMG_H));

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setBounds(0, 0, IMG_W, IMG_H);

        // Badge styling
        outOfStockBadge.setOpaque(true);
        outOfStockBadge.setBackground(new Color(200, 0, 0));
        outOfStockBadge.setForeground(Color.WHITE);
        outOfStockBadge.setFont(outOfStockBadge.getFont().deriveFont(Font.BOLD, 11f));
        outOfStockBadge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        outOfStockBadge.setVisible(false);

        // Add to container (badge on top of image)
        imageContainer.add(imageLabel);
        imageContainer.add(outOfStockBadge);

        add(imageContainer, BorderLayout.CENTER);

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

        // Load image
        loadImage();

        // Apply stock UI (badge + dim price)
        applyStockUi(imageContainer);

        // Tooltip
        setToolTipText(buildTooltip());
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
     * Applies visual UI changes based on stock.
     */
    private void applyStockUi(JPanel imageContainer) {
        boolean outOfStock = (product != null && product.getStock() <= 0);

        // Show badge if out of stock
        outOfStockBadge.setVisible(outOfStock);

        // Position badge (top-right)
        if (outOfStock) {
            Dimension pref = outOfStockBadge.getPreferredSize();
            int x = IMG_W - pref.width - 6;
            int y = 6;
            outOfStockBadge.setBounds(x, y, pref.width, pref.height);

            // Dim price text
            priceLabel.setForeground(Color.GRAY);
        } else {
            priceLabel.setForeground(Color.BLACK);
        }

        imageContainer.revalidate();
        imageContainer.repaint();
    }

    /**
     * Builds a nicer tooltip including stock status.
     */
    private String buildTooltip() {
        if (product == null) return "No product";
        String stockText = (product.getStock() <= 0) ? "Out of stock" : ("In stock: " + product.getStock());
        return "<html>"
                + "<b>" + safe(product.getDisplayName()) + "</b><br>"
                + "Price: $" + product.getPrice() + "<br>"
                + stockText
                + "</html>";
    }

    private String safe(String s) {
        return (s == null) ? "" : s.replace("<", "&lt;").replace(">", "&gt;");
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
