package store.gui.view;

import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class ProductPanel extends JPanel {

    private final Product product;

    private final JLabel imageLabel = new JLabel();
    private final JLabel nameLabel;
    private final JLabel priceLabel;

    // כדאי לשלוט בגודל כרטיס מוצר אחיד
    private static final int CARD_W = 170;
    private static final int CARD_H = 220;

    // השטח לתמונה בתוך הכרטיס (הטקסט יישב מתחת)
    private static final int IMG_W = 169;
    private static final int IMG_H = 219;

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

            ImageIcon icon = new ImageIcon(url);

            int w = 169;  // המידות שבחרת
            int h = 219;

            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);

            imageLabel.setText("");
            imageLabel.setIcon(new ImageIcon(scaled));

        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image error");
        }
    }


    public Product getProduct() {
        return product;
    }

}
