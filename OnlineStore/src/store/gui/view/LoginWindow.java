package store.gui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {

    private final JButton customerButton;
    private final JButton managerButton;

    public LoginWindow() {
        setTitle("Login");
        setSize(320, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

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

    public void addCustomerListener(ActionListener l) {
        customerButton.addActionListener(l);
    }

    public void addManagerListener(ActionListener l) {
        managerButton.addActionListener(l);
    }
}
