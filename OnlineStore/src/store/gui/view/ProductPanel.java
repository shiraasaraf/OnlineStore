/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.view;

import store.gui.controller.StoreController;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Swing panel that renders a single product card in the catalog grid.
 *
 * <p>
 * The panel displays the product image, name, and price. If a store-wide discount
 * is active, the displayed price reflects the discounted amount and the original
 * price may be shown as struck-through.
 * </p>
 *
 * <p>
 * If the product is out of stock, an "OUT OF STOCK" badge is displayed and the
 * text colors are dimmed.
 * </p>
 */
public class ProductPanel extends JPanel {

    /** Product represented by this panel. */
    private final Product product;

    /** Controller used for price calculations that depend on store configuration (may be {@code null}). */
    private final StoreController controller;

    /** Label used to display the product image. */
    private final JLabel imageLabel = new JLabel();

    /** Label used to display the product name. */
    private final JLabel nameLabel;

    /** Label used to display the discounted (or regular) price. */
    private final JLabel priceLabel;

    /** Label used to display original price (strikethrough) when discount is active. */
    private final JLabel originalPriceLabel;

    /** Badge displayed when the product is out of stock. */
    private final JLabel outOfStockBadge = new JLabel("OUT OF STOCK");

    /** Fixed card width. */
    private static final int CARD_W = 170;

    /** Fixed card height. */
    private static final int CARD_H = 240;

    /** Preferred image area width. */
    private static final int IMG_W = 169;

    /** Preferred image area height. */
    private static final int IMG_H = 219;

    /** Padding applied to the image bounds. */
    private static final int IMG_PAD = 8;

    /** Last scaled icon width used to avoid redundant rescaling. */
    private int lastScaledW = -1;

    /** Last scaled icon height used to avoid redundant rescaling. */
    private int lastScaledH = -1;

    /** Currency formatter used for displaying prices. */
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Constructs a product card panel for the given product.
     *
     * @param product    the product to display
     * @param controller the store controller used to compute discounted prices (may be {@code null})
     */
    public ProductPanel(Product product, StoreController controller) {
        this.product = product;
        this.controller = controller;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(CARD_W, CARD_H));
        setBackground(Color.WHITE);
        setOpaque(true);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLayeredPane imageLayer = new JLayeredPane() {
            @Override
            public void doLayout() {
                int w = getWidth();
                int h = getHeight();

                int innerW = Math.max(1, IMG_W - 2 * IMG_PAD);
                int innerH = Math.max(1, IMG_H - 2 * IMG_PAD);

                int x = (w - innerW) / 2;
                int y = (h - innerH) / 2;

                imageLabel.setBounds(x, y, innerW, innerH);

                Dimension pref = outOfStockBadge.getPreferredSize();
                int bx = w - pref.width - 6;
                int by = 6;
                outOfStockBadge.setBounds(bx, by, pref.width, pref.height);

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

        outOfStockBadge.setOpaque(true);
        outOfStockBadge.setBackground(new Color(200, 0, 0));
        outOfStockBadge.setForeground(Color.WHITE);
        outOfStockBadge.setFont(outOfStockBadge.getFont().deriveFont(Font.BOLD, 11f));
        outOfStockBadge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        outOfStockBadge.setVisible(false);

        imageLayer.add(imageLabel, JLayeredPane.DEFAULT_LAYER);
        imageLayer.add(outOfStockBadge, JLayeredPane.PALETTE_LAYER);

        add(imageLayer, BorderLayout.CENTER);

        nameLabel = new JLabel(product.getDisplayName(), SwingConstants.CENTER);

        priceLabel = new JLabel("", SwingConstants.CENTER);
        priceLabel.setFont(priceLabel.getFont().deriveFont(Font.BOLD));

        originalPriceLabel = new JLabel("", SwingConstants.CENTER);
        originalPriceLabel.setFont(originalPriceLabel.getFont().deriveFont(Font.PLAIN, 11f));
        originalPriceLabel.setForeground(Color.DARK_GRAY);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Color.WHITE);

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        originalPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 8, 6));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(originalPriceLabel);

        add(infoPanel, BorderLayout.SOUTH);

        loadImage();
        applyDiscountUi();
        applyStockUi();
        setToolTipText(buildTooltip());
    }

    /**
     * Returns the product represented by this panel.
     *
     * @return the product instance
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Updates the displayed price according to the currently active discount strategy.
     *
     * <p>
     * If an actual discount is applied, the original price is shown as struck-through
     * and the discount name is displayed next to it.
     * </p>
     */
    private void applyDiscountUi() {
        if (product == null) {
            priceLabel.setText("");
            originalPriceLabel.setText("");
            return;
        }

        double base = product.getPrice();
        double discounted = base;

        if (controller != null) {
            discounted = controller.getPriceAfterDiscount(product);
        }

        priceLabel.setText(currency.format(discounted));

        boolean hasDiscount = Math.abs(discounted - base) > 1e-9 && discounted < base;

        if (hasDiscount && controller != null) {
            String strategyName = controller.getDiscountDisplayName();
            originalPriceLabel.setText("<html><strike>" + currency.format(base) + "</strike> &nbsp;(" + strategyName + ")</html>");
        } else {
            originalPriceLabel.setText("");
        }
    }

    /**
     * Updates the stock-related UI state (badge visibility and text color).
     */
    private void applyStockUi() {
        boolean outOfStock = (product != null && product.getStock() <= 0);
        outOfStockBadge.setVisible(outOfStock);

        Color c = outOfStock ? Color.GRAY : Color.BLACK;
        priceLabel.setForeground(c);
        nameLabel.setForeground(outOfStock ? Color.GRAY : Color.BLACK);
        originalPriceLabel.setForeground(outOfStock ? Color.GRAY : Color.DARK_GRAY);
    }

    /**
     * Builds an HTML tooltip containing product name, price, stock information and description.
     *
     * @return an HTML tooltip string
     */
    private String buildTooltip() {
        if (product == null) return "No product";

        String stockText = (product.getStock() <= 0)
                ? "Out of stock"
                : ("In stock: " + product.getStock());

        String desc = product.getDescription();
        if (desc == null) desc = "";
        desc = desc.trim();
        if (desc.length() > 180) desc = desc.substring(0, 180) + "...";

        double base = product.getPrice();
        double discounted = (controller == null) ? base : controller.getPriceAfterDiscount(product);

        String priceText;
        if (Math.abs(discounted - base) > 1e-9 && discounted < base && controller != null) {
            priceText = currency.format(discounted) + " (" + controller.getDiscountDisplayName() + "), was " + currency.format(base);
        } else {
            priceText = currency.format(base);
        }

        return "<html>"
                + "<b>" + escapeHtml(product.getName()) + "</b><br>"
                + "Price: " + escapeHtml(priceText) + "<br>"
                + escapeHtml(stockText) + "<br><br>"
                + escapeHtml(desc)
                + "</html>";
    }

    /**
     * Escapes a string for safe insertion into an HTML tooltip.
     *
     * @param s the input string
     * @return the escaped HTML string (never {@code null})
     */
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /**
     * Loads the product image icon from the classpath using {@link Product#getImagePath()}.
     */
    private void loadImage() {
        if (product == null) return;
        String path = product.getImagePath();
        if (path == null || path.isBlank()) return;

        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) return;

        ImageIcon icon = new ImageIcon(url);
        imageLabel.setIcon(icon);
        lastScaledW = -1;
        lastScaledH = -1;
    }

    /**
     * Rescales the current image icon to match the given target bounds if needed.
     *
     * @param w target width in pixels
     * @param h target height in pixels
     */
    private void maybeRescaleIcon(int w, int h) {
        if (w <= 0 || h <= 0) return;
        if (w == lastScaledW && h == lastScaledH) return;

        Icon icon = imageLabel.getIcon();
        if (!(icon instanceof ImageIcon)) return;

        ImageIcon ii = (ImageIcon) icon;
        Image img = ii.getImage();
        if (img == null) return;

        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));

        lastScaledW = w;
        lastScaledH = h;
    }
}
