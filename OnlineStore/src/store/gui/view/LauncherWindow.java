/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.view;

import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.gui.controller.StoreController;
import store.io.OrderHistoryIO;
import store.order.Order;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Launcher window that opens Customer and Manager store windows.
 * <p>
 * All opened windows share the same {@link StoreEngine} instance.
 * On startup, order history is loaded from file into the engine.
 * </p>
 */
public class LauncherWindow extends JFrame {

    private final StoreEngine engine;

    private final JButton openCustomerButton = new JButton("Open Customer window");
    private final JButton openManagerButton = new JButton("Open Manager window");

    private final AtomicInteger customerCounter = new AtomicInteger(1);
    private boolean managerWindowOpen = false;

    /**
     * Constructs the launcher window and loads order history into the shared engine.
     *
     * @param engine shared store engine
     */
    public LauncherWindow(StoreEngine engine) {
        super("Online Store - Launcher");
        this.engine = engine;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 220);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        openCustomerButton.addActionListener(e -> openCustomerWindow());
        openManagerButton.addActionListener(e -> openManagerWindow());

        panel.add(openCustomerButton);
        panel.add(openManagerButton);

        add(panel, BorderLayout.CENTER);
    }


    /**
     * Opens a new customer store window after asking for a username.
     */
    private void openCustomerWindow() {
        String username = askUsername();
        if (username == null) {
            return;
        }

        Customer customer = new Customer(username, "");
        StoreController controller = new StoreController(engine, customer, null);

        StoreWindow window = new StoreWindow(controller);
        window.setTitle("Online Store - Customer (" + username + ")");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * Prompts the user for a non-empty username.
     *
     * @return username, or null if the dialog was cancelled
     */
    private String askUsername() {
        String suggested = "Guest-" + customerCounter.getAndIncrement();

        while (true) {
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Enter customer username:",
                    "Customer Login",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    suggested
            );

            if (input == null) {
                return null;
            }

            String username = input.trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Username cannot be empty.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                continue;
            }

            return username;
        }
    }

    /**
     * Opens the manager store window (only one can be open at a time).
     */
    private void openManagerWindow() {
        if (managerWindowOpen) {
            JOptionPane.showMessageDialog(
                    this,
                    "Manager window is already open.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        managerWindowOpen = true;
        openManagerButton.setEnabled(false);

        Customer customer = new Customer("Admin", "");
        Manager manager = new Manager("Admin", "");

        StoreController controller = new StoreController(engine, customer, manager);

        StoreWindow window = new StoreWindow(controller);
        window.setTitle("Online Store - Manager");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                managerWindowOpen = false;
                openManagerButton.setEnabled(true);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        window.setVisible(true);
    }
}
