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

    /** Image area target size (like your old sizes). */
    private static final int IMG_W = 169;
    private static final int IMG_H = 219;

    /** Padding between image and the white frame. */
    private static final int IMG_PAD = 8;

    /** Keep icon scaled to these inner bounds. */
    private int lastScaledW = -1;
    private int lastScaledH = -1;

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

        //hand cursor when hovering over the product card
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        // ---- Image area (centered properly even when panel is wider) ----
        JLayeredPane imageLayer = new JLayeredPane() {
            @Override
            public void doLayout() {
                int w = getWidth();
                int h = getHeight();

                // inner image box size (fixed, so it always looks consistent)
                int innerW = Math.max(1, IMG_W - 2 * IMG_PAD);
                int innerH = Math.max(1, IMG_H - 2 * IMG_PAD);

                // center the image box inside available space
                int x = (w - innerW) / 2;
                int y = (h - innerH) / 2;

                imageLabel.setBounds(x, y, innerW, innerH);

                // badge top-right (relative to the whole imageLayer)
                Dimension pref = outOfStockBadge.getPreferredSize();
                int bx = w - pref.width - 6;
                int by = 6;
                outOfStockBadge.setBounds(bx, by, pref.width, pref.height);

                // re-scale icon only if size changed
                maybeRescaleIcon(innerW, innerH);
            }
        };
        imageLayer.setOpaque(true);
        imageLayer.setBackground(Color.WHITE);
        imageLayer.setPreferredSize(new Dimension(IMG_W, IMG_H));

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);

        // Badge styling
        outOfStockBadge.setOpaque(true);
        outOfStockBadge.setBackground(new Color(200, 0, 0));
        outOfStockBadge.setForeground(Color.WHITE);
        outOfStockBadge.setFont(outOfStockBadge.getFont().deriveFont(Font.BOLD, 11f));
        outOfStockBadge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        outOfStockBadge.setVisible(false);

        imageLayer.add(imageLabel, JLayeredPane.DEFAULT_LAYER);
        imageLayer.add(outOfStockBadge, JLayeredPane.PALETTE_LAYER);

        add(imageLayer, BorderLayout.CENTER);

        // ---- Bottom info ----
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

        // Load image (icon will be scaled on first doLayout)
        loadImage();

        // Stock UI (badge + dim price)
        applyStockUi();

        // Tooltip
        setToolTipText(buildTooltip());
    }

    /**
     * Returns the currently displayed product.
     *
     * @return the current {@link Product}, or {@code null} if no product is selected
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Updates the stock-related UI elements based on the current product state.
     * <p>
     * If the product is out of stock (stock &lt;= 0), an "out of stock" badge is shown
     * and the price label is displayed in a muted (gray) color. Otherwise, the badge
     * is hidden and the price label is displayed in its normal color.
     * </p>
     * <p>
     * If no product is currently selected, the badge is hidden and the price label
     * is displayed using the default color.
     * </p>
     */
    private void applyStockUi() {
        boolean outOfStock = (product != null && product.getStock() <= 0);
        outOfStockBadge.setVisible(outOfStock);
        priceLabel.setForeground(outOfStock ? Color.GRAY : Color.BLACK);
    }

    /**
     * Builds an HTML tooltip describing the current product.
     * <p>
     * The tooltip includes the product name, price, stock status, and a short
     * description. The description is trimmed and truncated to a maximum length
     * to keep the tooltip readable. All dynamic text is safely escaped to prevent
     * malformed HTML rendering.
     * </p>
     *
     * @return an HTML-formatted tooltip string, or {@code "No product"} if no product
     *         is currently selected
     */
    private String buildTooltip() {
        if (product == null) return "No product";

        String stockText = (product.getStock() <= 0)
                ? "Out of stock"
                : ("In stock: " + product.getStock());

        String desc = product.getDescription();
        if (desc == null) desc = "";
        desc = desc.trim();
        if (desc.isEmpty()) desc = "-";


        int maxLen = 160;
        if (desc.length() > maxLen) {
            desc = desc.substring(0, maxLen) + "...";
        }

        return "<html>"
                + "<b>" + safe(product.getDisplayName()) + "</b><br>"
                + "Price: $" + product.getPrice() + "<br>"
                + stockText + "<br>"
                + "<b>Description:</b> " + safe(desc)
                + "</html>";
    }

    /**
     * Safely escapes basic HTML special characters in a string.
     * <p>
     * This method replaces the characters '&lt;' and '&gt;' with their corresponding
     * HTML entities to prevent malformed HTML or unintended markup when rendering
     * text inside HTML-based Swing components (such as {@link JLabel} with HTML content).
     * If the input string is {@code null}, an empty string is returned.
     * </p>
     *
     * @param s the input string to escape (may be {@code null})
     * @return a non-null string with basic HTML characters escaped
     */
    private String safe(String s) {
        return (s == null) ? "" : s.replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Loads the product image (unscaled). Scaling is done in doLayout().
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
            imageLabel.setText("");
            imageLabel.setIcon(icon);

            // reset scaling cache so it will rescale on first layout
            lastScaledW = -1;
            lastScaledH = -1;

        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image error");
        }
    }

    /**
     * Scale icon only when needed (called from doLayout()).
     */
    private void maybeRescaleIcon(int targetW, int targetH) {
        Icon ic = imageLabel.getIcon();
        if (!(ic instanceof ImageIcon)) return;

        if (targetW <= 0 || targetH <= 0) return;
        if (targetW == lastScaledW && targetH == lastScaledH) return;

        ImageIcon icon = (ImageIcon) ic;
        Image scaled = icon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));

        lastScaledW = targetW;
        lastScaledH = targetH;
    }
}
