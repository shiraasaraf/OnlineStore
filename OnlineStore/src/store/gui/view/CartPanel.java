package store.gui.view;

import store.cart.CartItem;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CartPanel extends JPanel {

    // אזור שמכיל שורות של פריטים
    private final JPanel itemsPanel;

    private final JLabel totalLabel;
    private final JButton checkoutButton;

    // ActionListener להסרה (ה-Controller יתקין)
    private ActionListener removeListener;

    public CartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(itemsPanel);
        add(scroll, BorderLayout.CENTER);

        totalLabel = new JLabel("Total: $0.00");
        checkoutButton = new JButton("Checkout");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(checkoutButton, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * ה-Controller יקרא לזה אחרי כל שינוי בעגלה
     */
    public void setItems(List<CartItem> items) {
        itemsPanel.removeAll();

        double total = 0.0;

        for (CartItem item : items) {
            itemsPanel.add(createRow(item));
            total += item.getTotalPrice();
        }

        totalLabel.setText("Total: $" + total);

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    /**
     * Hook: ה-Controller מתקין listener אחד, וכל כפתור "Remove" יפעיל אותו.
     * ה-Controller יבדוק event.getActionCommand() כדי לדעת איזה מוצר להסיר.
     */
    public void addRemoveItemListener(ActionListener l) {
        this.removeListener = l;
    }

    /**
     * Hook: ה-Controller מתקין listener לכפתור checkout.
     */
    public void addCheckoutListener(ActionListener l) {
        checkoutButton.addActionListener(l);
    }

    // ---- private helpers ----

    private JPanel createRow(CartItem item) {
        Product p = item.getProduct();

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // שמאל: שם + מחיר יחידה
        String name = (p == null) ? "Unknown" : p.getDisplayName();
        double unitPrice = (p == null) ? 0.0 : p.getPrice();

        JLabel info = new JLabel(name + " | Unit: $" + unitPrice + " | Qty: " + item.getQuantity());
        row.add(info, BorderLayout.CENTER);

        // ימין: מחיר כולל + כפתור הסרה
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel lineTotal = new JLabel("$" + item.getTotalPrice());

        JButton removeButton = new JButton("Remove");
        // מזהה לפריט: נשתמש בשם מוצר (או משהו ייחודי יותר אם יש לך ID)
        removeButton.setActionCommand(name);

        if (removeListener != null) {
            removeButton.addActionListener(removeListener);
        }

        right.add(lineTotal);
        right.add(removeButton);

        row.add(right, BorderLayout.EAST);

        return row;
    }
}
