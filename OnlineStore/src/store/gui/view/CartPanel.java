/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import store.cart.CartItem;
import store.products.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Swing panel that displays the shopping cart contents.
 * <p>
 * Shows a list of cart items, total price, and a checkout button.
 * The controller attaches listeners for removing items and for checkout.
 * </p>
 */
public class CartPanel extends JPanel {

    /** Panel that holds item rows. */
    private final JPanel itemsPanel;

    /** Displays the current cart total. */
    private final JLabel totalLabel;

    /** Checkout button. */
    private final JButton checkoutButton;

    /** Listener used for "Remove" buttons (installed by the controller). */
    private ActionListener removeListener;

    /**
     * Constructs an empty cart panel UI.
     */
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
     * Updates the displayed cart items and total.
     *
     * @param items cart items to display
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
     * Installs a listener for item removal actions.
     * <p>
     * The listener is triggered when any "Remove" button is pressed.
     * </p>
     *
     * @param l listener to install
     */
    public void addRemoveItemListener(ActionListener l) {
        this.removeListener = l;
    }

    /**
     * Installs a listener for the checkout button.
     *
     * @param l listener to install
     */
    public void addCheckoutListener(ActionListener l) {
        checkoutButton.addActionListener(l);
    }

    // ---- private helpers ----

    /**
     * Creates a UI row for a single cart item.
     *
     * @param item cart item
     * @return row panel
     */
    private JPanel createRow(CartItem item) {
        Product p = item.getProduct();

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        String name = (p == null) ? "Unknown" : p.getDisplayName();
        double unitPrice = (p == null) ? 0.0 : p.getPrice();

        JLabel info = new JLabel(name + " | Unit: $" + unitPrice + " | Qty: " + item.getQuantity());
        row.add(info, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel lineTotal = new JLabel("$" + item.getTotalPrice());

        JButton removeButton = new JButton("Remove");
        removeButton.putClientProperty("product", p);

        removeButton.addActionListener(e -> {
            if (removeListener != null) {
                removeListener.actionPerformed(e);
            }
        });

        right.add(lineTotal);
        right.add(removeButton);

        row.add(right, BorderLayout.EAST);

        return row;
    }
}
