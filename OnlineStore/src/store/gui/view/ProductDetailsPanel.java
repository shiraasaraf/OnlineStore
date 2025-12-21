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
 * A Swing panel that displays details of the currently selected product.
 * <p>
 * Displays the product name, description, price, stock amount, product image,
 * and an "Add to Cart" button.
 * </p>
 */
public class ProductDetailsPanel extends JPanel {

    private Product product;

    // UI components
    private final JLabel imageLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel priceLabel = new JLabel();
    private final JLabel stockLabel = new JLabel();

    private JLabel descriptionLabel;
    private final JButton addToCartButton = new JButton("Add to Cart");
    private final JLabel feedbackLabel = new JLabel(" ");

    // Image display size (match your product tile proportions)
    private static final int IMG_W = 169;
    private static final int IMG_H = 219;

    public ProductDetailsPanel(Product product) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Product Details"));

        // image
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(IMG_W, IMG_H));
        imageLabel.setMinimumSize(new Dimension(IMG_W, IMG_H));
        imageLabel.setMaximumSize(new Dimension(IMG_W, IMG_H));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);

        add(imageLabel);
        add(Box.createVerticalStrut(8));

        // labels
        add(nameLabel);
        add(priceLabel);
        add(stockLabel);

        add(Box.createVerticalStrut(8));

        // description
        descriptionLabel = new JLabel();
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // מאפשר עטיפת שורות ל־JLabel
        descriptionLabel.setText("<html>Description: </html>");

        add(descriptionLabel);

        add(Box.createVerticalStrut(8));

        // add-to-cart button (quantity moved to cart)
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomRow.add(addToCartButton);
        add(bottomRow);

        // feedback message
        feedbackLabel.setForeground(new Color(0, 128, 0));
        add(feedbackLabel);

        // init
        setProduct(product);
    }

    /**
     * Replaces the currently displayed product and refreshes the UI.
     *
     * @param product the product to display (may be {@code null})
     */
    public void setProduct(Product product) {
        this.product = product;
        refreshView();
    }

    /**
     * Returns the currently displayed product.
     *
     * @return the current product (may be {@code null})
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Registers a listener for the "Add to Cart" button.
     *
     * @param listener the action listener to register
     */
    public void addAddToCartListener(ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }

    /**
     * Shows a short feedback message after a successful add-to-cart action.
     */
    public void showAddedFeedback() {
        feedbackLabel.setText("Added to cart!");
        Timer t = new Timer(1000, e -> feedbackLabel.setText(" "));
        t.setRepeats(false);
        t.start();
    }

    // --- internal UI update ---

    private void refreshView() {
        if (product == null) {
            nameLabel.setText("Name: -");
            priceLabel.setText("Price: -");
            stockLabel.setText("Stock: -");
            descriptionLabel.setText("Description: -");



            imageLabel.setIcon(null);
            imageLabel.setText("No Image");
            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            imageLabel.setVerticalTextPosition(SwingConstants.CENTER);

            addToCartButton.setEnabled(false);
            feedbackLabel.setText(" ");
            return;
        }

        // text fields
        nameLabel.setText("Name: " + product.getDisplayName());
        priceLabel.setText("Price: " + product.getPrice() + "$");
        stockLabel.setText("Stock: " + product.getStock());

        String desc = product.getDescription() == null ? "" : product.getDescription();
        descriptionLabel.setText("<html><b>Description:</b> " + desc + "</html>");

        // image
        loadProductImage();

        // enable/disable add button
        addToCartButton.setEnabled(product.getStock() > 0);

        feedbackLabel.setText(" ");
    }

    private void loadProductImage() {
        // התאימי לפי איך שאת שומרת נתיב תמונה במוצר
        // אם אצלך זה getImagePath() – זה המצב הנפוץ אצלך בפרויקט
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
}
