package store.gui.view;


import store.cart.CartItem;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CartPanel extends JPanel {
    private JTextArea cartArea;
    private JLabel totalLabel;

    public CartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        cartArea = new JTextArea();
        cartArea.setEditable(false);

        totalLabel = new JLabel("Total: 0.00₪");

        add(new JScrollPane(cartArea), BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
    }

    public void updateCart(List<CartItem> items) {
        cartArea.setText("");
        double total = 0.0;

        for (CartItem item : items) {
            cartArea.append(
                    item.getQuantity() + " " + item.getProduct() + " - " + item.getTotalPrice() + "₪\n"
            );
            total += item.getTotalPrice();
        }

        totalLabel.setText("Total: " + total + "₪");
    }
}
