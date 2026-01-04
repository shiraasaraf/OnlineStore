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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Launcher window that opens Customer and Manager store windows.
 * <p>
 * The launcher acts as the entry point to the GUI: it allows opening multiple
 * customer windows and a single manager window. All opened windows share the
 * same {@link StoreEngine} instance, so changes performed by one window (e.g.,
 * purchases or inventory updates) are reflected across all other windows after refresh.
 * </p>
 * <p>
 * Note: This class does not load order history by itself. Any loading of persisted
 * data (catalog/orders) should be performed by the application startup code before
 * creating this window, or inside the {@link StoreEngine}/{@link StoreController}
 * initialization if implemented there.
 * </p>
 */
public class LauncherWindow extends JFrame {

    /** Shared store engine instance used by all windows. */
    private final StoreEngine engine;

    /** Opens a new customer window. */
    private final JButton openCustomerButton = new JButton("Open Customer window");

    /** Opens the manager window (only one may be open at a time). */
    private final JButton openManagerButton = new JButton("Open Manager window");

    /** Counter used to generate unique default guest usernames. */
    private final AtomicInteger customerCounter = new AtomicInteger(1);

    /** True while a manager window is open, to prevent opening multiple manager windows. */
    private boolean managerWindowOpen = false;

    /**
     * Constructs the launcher window.
     * <p>
     * This window provides two actions: opening a customer store window and opening
     * a manager store window. Customer windows can be opened multiple times, while
     * the manager window is restricted to a single instance at a time.
     * </p>
     *
     * @param engine the shared store engine used by all opened windows
     * @throws IllegalArgumentException if {@code engine} is {@code null}
     */
    public LauncherWindow(StoreEngine engine) {
        super("Online Store - Launcher");

        if (engine == null) {
            throw new IllegalArgumentException("engine cannot be null");
        }
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
     * Opens a new customer store window.
     * <p>
     * Prompts the user for a non-empty username. If the user cancels the dialog,
     * no window is opened. Each customer window receives its own {@link StoreController}
     * instance but shares the same {@link StoreEngine}.
     * </p>
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
     * <p>
     * Displays an input dialog with a suggested guest username (e.g., "Guest-1").
     * The dialog repeats until the user provides a non-empty value or cancels.
     * </p>
     *
     * @return the entered username, or {@code null} if the dialog was cancelled
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
     * Opens the manager store window.
     * <p>
     * Only one manager window may be open at a time. While it is open, the
     * "Open Manager window" button is disabled. When the manager window is closed,
     * the button is re-enabled and another manager window may be opened.
     * </p>
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

        /**
         * Tracks the manager window lifecycle.
         * <p>
         * When the manager window is closed, this listener clears the "manager open"
         * flag and re-enables the manager button in the launcher.
         * </p>
         */
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                managerWindowOpen = false;
                openManagerButton.setEnabled(true);
            }
        });

        window.setVisible(true);
    }
}
