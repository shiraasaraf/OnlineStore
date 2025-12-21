package store.gui.view;

import store.products.Product;

import javax.swing.*;
import java.awt.*;
import  java.net.URL;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;


public class ProductPanel extends JPanel {

    private final Product product;

    private final JLabel imageLabel = new JLabel();
    private final JLabel nameLabel;
    private final JLabel priceLabel;

    // כדאי לשלוט בגודל כרטיס מוצר אחיד
    private static final int CARD_W = 170;
    private static final int CARD_H = 220;

    // השטח לתמונה בתוך הכרטיס (הטקסט יישב מתחת)
    private static final int IMG_W = 168;
    private static final int IMG_H = 218;

    public ProductPanel(Product product) {
        this.product = product;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(CARD_W, CARD_H));
        setBackground(Color.WHITE);
        setOpaque(true);

        // --- תמונה ---
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setPreferredSize(new Dimension(IMG_W, IMG_H));

        // חשוב: התמונה באמצע ולא למעלה
        add(imageLabel, BorderLayout.CENTER);

        // --- טקסט למטה (שם + מחיר יחד) ---
        nameLabel = new JLabel(product.getDisplayName(), SwingConstants.CENTER);
        priceLabel = new JLabel("$" + product.getPrice(), SwingConstants.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Color.WHITE);

        // כדי שיישב יפה במרכז רוחבית
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // (אופציונלי) קצת ריווח פנימי
        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 8, 6));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(priceLabel);

        // הטקסט תמיד למטה צמוד לתחתית המסגרת
        add(infoPanel, BorderLayout.SOUTH);

        setToolTipText(product.toString());

        loadImage();
    }



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

            ImageIcon originalIcon = new ImageIcon(url);

            int maxW = 168; // המידות שקבעת
            int maxH = 218;

            imageLabel.setText("");
            imageLabel.setIcon(scaleHighQuality(originalIcon, maxW, maxH));

        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image error");
        }
    }

    private ImageIcon scaleHighQuality(ImageIcon src, int maxW, int maxH) {
        int w = src.getIconWidth();
        int h = src.getIconHeight();

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        scale = Math.min(scale, 1.0); // לא להגדיל מעבר למקור

        int newW = (int) Math.round(w * scale);
        int newH = (int) Math.round(h * scale);

        BufferedImage out =
                new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(src.getImage(), 0, 0, newW, newH, null);
        g2.dispose();

        return new ImageIcon(out);
    }



    //soft scale for better interpolation and minimize pixels
    private ImageIcon scaleToFit(Image src, int maxW, int maxH) {
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        if (w <= 0 || h <= 0) return new ImageIcon(src);

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        int newW = Math.max(1, (int) Math.round(w * scale));
        int newH = Math.max(1, (int) Math.round(h * scale));

        // סקייל ראשוני
        Image scaled = src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

        // ציור מחדש עם רמזים איכותיים (מפחית פיקסול)
        BufferedImage out = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();

        return new ImageIcon(out);
    }


    public Product getProduct() {
        return product;
    }

}
