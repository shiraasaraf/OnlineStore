/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.products.Product;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Panel that displays details of the selected product.
 * <p>
 * Shows product fields (name, price, stock, description), product image,
 * and an "Add to Cart" button.
 * Also disables Add to Cart when out of stock and shows a clear status message.
 * </p>
 */
public class ProductDetailsPanel extends JPanel {

    /** Currently displayed product (may be null). */
    private Product product;

    /** Image label. */
    private final JLabel imageLabel = new JLabel();

    /** Name label. */
    private final JLabel nameLabel = new JLabel();

    /** Price label. */
    private final JLabel priceLabel = new JLabel();

    /** Stock label. */
    private final JLabel stockLabel = new JLabel();

    /** Description label (HTML). */
    private JLabel descriptionLabel;

    /** Stock status message (e.g., OUT OF STOCK). */
    private final JLabel stockStatusLabel = new JLabel(" ");

    /** Add-to-cart button. */
    private final JButton addToCartButton = new JButton("Add to Cart");

    /** Feedback message label. */
    private final JLabel feedbackLabel = new JLabel(" ");

    /** Fixed image display size. */
    private static final int IMG_W = 169;

    /** Fixed image display size. */
    private static final int IMG_H = 219;

    /**
     * Constructs a product details panel.
     *
     * @param product initial product to display (may be null)
     */
    public ProductDetailsPanel(Product product) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Product Details"));

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(IMG_W, IMG_H));
        imageLabel.setMinimumSize(new Dimension(IMG_W, IMG_H));
        imageLabel.setMaximumSize(new Dimension(IMG_W, IMG_H));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);

        add(imageLabel);
        add(Box.createVerticalStrut(8));

        add(nameLabel);
        add(priceLabel);
        add(stockLabel);

        // Stock status line (Out of stock)
        stockStatusLabel.setForeground(new Color(180, 0, 0));
        stockStatusLabel.setFont(stockStatusLabel.getFont().deriveFont(Font.BOLD));
        add(stockStatusLabel);

        add(Box.createVerticalStrut(8));

        descriptionLabel = new JLabel();
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionLabel.setText("<html>Description: </html>");
        add(descriptionLabel);

        add(Box.createVerticalStrut(8));

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomRow.add(addToCartButton);
        add(bottomRow);

        feedbackLabel.setForeground(new Color(0, 128, 0));
        add(feedbackLabel);

        setProduct(product);
    }

    /**
     * Sets the product to display and refreshes the UI.
     *
     * @param product the product (may be null)
     */
    public void setProduct(Product product) {
        this.product = product;
        refreshView();
    }

    /**
     * Returns the currently displayed product.
     *
     * @return the current product (may be null)
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Adds a listener to the "Add to Cart" button.
     *
     * @param listener action listener
     */
    public void addAddToCartListener(ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }

    /**
     * Shows a temporary "added to cart" feedback message.
     */
    public void showAddedFeedback() {
        feedbackLabel.setText("Added to cart!");
        Timer t = new Timer(1000, e -> feedbackLabel.setText(" "));
        t.setRepeats(false);
        t.start();
    }

    /**
     * Refreshes all fields based on the current product.
     */
    private void refreshView() {
        // Always clear feedback on selection change
        feedbackLabel.setText(" ");

        if (product == null) {
            nameLabel.setText("Name: -");
            priceLabel.setText("Price: -");
            stockLabel.setText("Stock: -");
            stockStatusLabel.setText(" ");
            descriptionLabel.setText("<html><b>Description:</b> -</html>");

            imageLabel.setIcon(null);
            imageLabel.setText("No Image");
            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            imageLabel.setVerticalTextPosition(SwingConstants.CENTER);

            addToCartButton.setEnabled(false);
            return;
        }

        nameLabel.setText("Name: " + safeText(product.getDisplayName()));
        priceLabel.setText("Price: " + product.getPrice() + "$");
        stockLabel.setText("Stock: " + product.getStock());

        String desc = (product.getDescription() == null) ? "" : product.getDescription();
        descriptionLabel.setText("<html><b>Description:</b> " + safeHtml(desc) + "</html>");

        loadProductImage();

        boolean inStock = product.getStock() > 0;
        addToCartButton.setEnabled(inStock);

        if (!inStock) {
            stockStatusLabel.setText("OUT OF STOCK");
        } else {
            stockStatusLabel.setText(" ");
        }
    }

    /**
     * Loads and scales the product image into the image label.
     */
    private void loadProductImage() {
        String path;
        try {
            path = product.getImagePath();
        } catch (Exception e) {
            path = null;
        }

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

        try {
            BufferedImage original = ImageIO.read(url);
            if (original == null) {
                imageLabel.setIcon(null);
                imageLabel.setText("Invalid image");
                return;
            }

            Image scaled = original.getScaledInstance(IMG_W, IMG_H, Image.SCALE_SMOOTH);
            imageLabel.setText("");
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (IOException ex) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image error");
        }
    }

    /**
     * Safely formats plain text (non-HTML).
     */
    private String safeText(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * Escapes minimal HTML chars for safe rendering inside HTML labels.
     */
    private String safeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
