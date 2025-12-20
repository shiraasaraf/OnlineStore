package store.gui.view;

import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ProductDetailsPanel extends JPanel {

    private Product product;

    //Components that will be updated when we replace a product
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel stockLabel;

    private JTextArea descriptionArea;
    private JButton addToCartButton;

    private JSpinner quantitySpinner;
    private JLabel feedbackLabel;

    public ProductDetailsPanel(Product product) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Product Details"));

        //labels
        nameLabel = new JLabel();
        priceLabel = new JLabel();
        stockLabel = new JLabel();

        add(nameLabel);
        add(priceLabel);
        add(stockLabel);

        add(Box.createVerticalStrut(8));

        //description
        descriptionArea = new JTextArea();
        descriptionArea.setText(""); // ✅ לא ניגשים ל-product בבנאי
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setRows(6);

        add(new JScrollPane(descriptionArea));


        add(Box.createVerticalStrut(8));

        // quantity selector + add-to-cart button (horizontal row)
        JPanel bottomRow = new JPanel(new FlowLayout (FlowLayout.LEFT));
        bottomRow.add(new JLabel("Quantity:"));

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        bottomRow.add(quantitySpinner);

        addToCartButton = new JButton("Add to Cart");
        bottomRow.add(addToCartButton);

        add(bottomRow);

        //feedback - empty label until item will be added to cart (timer will work)
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setForeground(new Color(0, 128, 0));
        add(feedbackLabel);

        //set initial product (updates UI)
        setProduct(product);
    }

    //allows to replace a product without rebuilding a panel
    public void setProduct(Product product) {
        this.product = product;
        refreshView();
    }

    private void refreshView() {
        if (product == null) {
            nameLabel.setText("Name: -");
            priceLabel.setText("Price: -");
            stockLabel.setText("Stock: - ");
            descriptionArea.setText("");
            addToCartButton.setEnabled(false);
            quantitySpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
            return;
        }

        nameLabel.setText("Name: " + product.getDisplayName());
        priceLabel.setText("Price: $" + product.getPrice());
        stockLabel.setText("Stock: " + product.getStock());
        //אופרטור טרנרי - ביטוי מקוצר לIF. אם התיאור של המוצר הוא null - תוחזר מחרוזת ריקה, אחרת יוחזר הערך שלו
        descriptionArea.setText(product.getDescription() == null ? "" : product.getDescription());
        descriptionArea.setCaretPosition(0);


        int stock = product.getStock();

        //כפתור להוספת כמות ממוצר יופעל אם יש סטוק
        if (stock > 0) {
            addToCartButton.setEnabled(true);
            quantitySpinner.setModel(new SpinnerNumberModel(1, 1, stock, 1));
        } else {
            addToCartButton.setEnabled(false);
            quantitySpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        }

        feedbackLabel.setText(" ");
    }


    public void addAddToCartListener(ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }


    public Product getProduct() {
        return product;
    }

    // הכמות שהמשתמש בחר (ה-Controller ישתמש בזה)
    public int getSelectedQuantity() {
        Object v = quantitySpinner.getValue();
        //אופרטור טרנרי - ביטוי מקוצר לIF. אם V הוא מסוג אינטג'ר- יוחזר הערך שלו, אם לא- יוחזר 1
        return (v instanceof Integer) ? (Integer) v : 1;
    }

    // להציג הודעה אחרי שה-Controller הצליח להוסיף לעגלה
    public void showAddedFeedback() {
        feedbackLabel.setText("Added to cart!");
        Timer t = new Timer(1000, e -> feedbackLabel.setText(" "));
        t.setRepeats(false);
        t.start();
    }
}
