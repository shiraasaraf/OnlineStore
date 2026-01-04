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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Swing panel that displays the shopping cart contents.
 * <p>
 * Shows cart items, total price, and a checkout button.
 * A controller can attach listeners for removing items and for checkout.
 * </p>
 */
public class CartPanel extends JPanel {

    /** Key used to store the {@link CartItem} on the remove button via client properties. */
    public static final String PROP_CART_ITEM = "cartItem";

    /** Panel that holds item rows. */
    private final JPanel itemsPanel;

    /** Displays the current cart total. */
    private final JLabel totalLabel;

    /** Checkout button. */
    private final JButton checkoutButton;

    /** Listener used for "Remove" buttons (installed by the controller). */
    private ActionListener removeListener;

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Constructs an empty cart panel UI.
     */
    public CartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);

        totalLabel = new JLabel("Total: " + currency.format(0.0));
        checkoutButton = new JButton("Checkout");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(checkoutButton, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Updates the displayed cart items and total.
     *
     * @param items cart items to display (not modified by this component)
     */
    public void setItems(List<CartItem> items) {
        itemsPanel.removeAll();

        double total = 0.0;
        for (CartItem item : items) {
            itemsPanel.add(createRow(item));
            total += item.getTotalPrice();
        }

        totalLabel.setText("Total: " + currency.format(total));

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    /**
     * Installs a listener for item removal actions.
     * <p>
     * When a "Remove" button is pressed, the event source is the button.
     * The controller can retrieve the related {@link CartItem} using:
     * {@code ((JButton)e.getSource()).getClientProperty(PROP_CART_ITEM)}.
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

        JLabel info = new JLabel(name + " | Unit: " + currency.format(unitPrice) + " | Qty: " + item.getQuantity());
        row.add(info, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel lineTotal = new JLabel(currency.format(item.getTotalPrice()));

        JButton removeButton = new JButton("Remove");
        removeButton.putClientProperty(PROP_CART_ITEM, item);

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
