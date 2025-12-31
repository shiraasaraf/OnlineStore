package store.gui.view;

import store.core.Customer;
import store.core.Manager;
import store.engine.StoreEngine;
import store.gui.controller.StoreController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A persistent launcher window that can open multiple customer windows
 * and a manager window, all sharing the same StoreEngine instance.
 */
public class LauncherWindow extends JFrame {

    private final StoreEngine engine;

    private final JButton openCustomerButton = new JButton("Open new Customer window");
    private final JButton openManagerButton = new JButton("Open Manager window");

    private final AtomicInteger customerCounter = new AtomicInteger(1);
    private volatile boolean managerWindowOpen = false;

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

    private void openCustomerWindow() {
        int id = customerCounter.getAndIncrement();

        Customer customer = new Customer("Guest-" + id, "guest" + id + "@example.com");
        Manager manager = null;

        StoreController controller = new StoreController(engine, customer, manager);

        StoreWindow window = new StoreWindow(controller);
        window.setTitle("Online Store - Customer #" + id);

        // IMPORTANT: customer windows must not close the whole app
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        window.setVisible(true);
    }

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

        Customer customer = new Customer("Guest", "guest@example.com");
        Manager manager = new Manager("Admin", "admin@example.com");

        StoreController controller = new StoreController(engine, customer, manager);

        StoreWindow window = new StoreWindow(controller);
        window.setTitle("Online Store - Manager");

        // IMPORTANT: manager window must not close the whole app
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Re-enable manager button when window closes
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                managerWindowOpen = false;
                openManagerButton.setEnabled(true);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // Ensures windowClosed is triggered reliably
                window.dispose();
            }
        });

        window.setVisible(true);
    }
}
