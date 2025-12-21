/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.gui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Login window for choosing application mode.
 * <p>
 * Allows the user to enter the system either as a customer
 * or as a manager.
 * </p>
 */
public class LoginWindow extends JFrame {

    /** Button for customer mode. */
    private final JButton customerButton;

    /** Button for manager mode. */
    private final JButton managerButton;

    /**
     * Constructs the login window UI.
     */
    public LoginWindow() {
        setTitle("Login");
        setSize(320, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JLabel title = new JLabel("Choose mode", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 10, 10));
        customerButton = new JButton("Enter as Customer");
        managerButton = new JButton("Enter as Manager");

        buttons.add(customerButton);
        buttons.add(managerButton);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        center.add(buttons, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    /**
     * Adds a listener for the customer button.
     *
     * @param l action listener
     */
    public void addCustomerListener(ActionListener l) {
        customerButton.addActionListener(l);
    }

    /**
     * Adds a listener for the manager button.
     *
     * @param l action listener
     */
    public void addManagerListener(ActionListener l) {
        managerButton.addActionListener(l);
    }
}
