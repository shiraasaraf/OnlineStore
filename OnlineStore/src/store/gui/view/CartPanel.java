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
 * Shows cart items, subtotal/discount/final total, and a checkout button.
 * A controller can attach listeners for removing items and for checkout.
 * </p>
 */
public class CartPanel extends JPanel {

    /** Key used to store the {@link CartItem} on the remove button via client properties. */
    public static final String PROP_CART_ITEM = "cartItem";

    /** Panel that holds item rows. */
    private final JPanel itemsPanel;

    /** Displays the current cart subtotal. */
    private final JLabel subtotalLabel;

    /** Displays the current cart discount (amount + strategy name). */
    private final JLabel discountLabel;

    /** Displays the current cart final total (after discount). */
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

        subtotalLabel = new JLabel("Subtotal: " + currency.format(0.0));
        discountLabel = new JLabel("Discount: " + currency.format(0.0) + " (No discount)");
        totalLabel = new JLabel("Total: " + currency.format(0.0));

        checkoutButton = new JButton("Checkout");

        JPanel totals = new JPanel();
        totals.setLayout(new BoxLayout(totals, BoxLayout.Y_AXIS));
        totals.add(subtotalLabel);
        totals.add(discountLabel);
        totals.add(totalLabel);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(totals, BorderLayout.WEST);
        bottom.add(checkoutButton, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Updates the displayed cart items (rows).
     * <p>
     * Note: Totals can be set accurately using {@link #setTotals(double, String, double)}.
     * This method keeps a safe default behavior (no-discount) if called alone.
     * </p>
     *
     * @param items cart items to display (not modified by this component)
     */
    public void setItems(List<CartItem> items) {
        itemsPanel.removeAll();

        double subtotal = 0.0;
        for (CartItem item : items) {
            itemsPanel.add(createRow(item));
            subtotal += item.getTotalPrice();
        }

        // Default (no discount) if totals aren't provided by the window/controller.
        setTotals(subtotal, "No discount", subtotal);

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    /**
     * Sets subtotal/discount/final total labels.
     *
     * @param subtotal     subtotal before discount
     * @param discountText discount strategy display name (e.g. "10% off")
     * @param finalTotal   total after discount
     */
    public void setTotals(double subtotal, String discountText, double finalTotal) {
        double discountAmount = Math.max(0.0, subtotal - finalTotal);

        subtotalLabel.setText("Subtotal: " + currency.format(subtotal));
        discountLabel.setText("Discount: " + currency.format(discountAmount)
                + " (" + (discountText == null ? "" : discountText) + ")");
        totalLabel.setText("Total: " + currency.format(finalTotal));
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
