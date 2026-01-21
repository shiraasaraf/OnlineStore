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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LauncherWindow is the entry point of the GUI.
 *
 * <p>
 * It allows opening:
 * </p>
 * <ul>
 *   <li>Multiple <b>Customer</b> store windows (each customer gets its own {@link StoreController}).</li>
 *   <li>A single <b>Manager</b> store window (Singleton) via {@link StoreWindow#openManagerWindow(StoreController)}.</li>
 * </ul>
 *
 * <p>
 * All opened windows share the same {@link StoreEngine} instance, so changes performed by one window
 * (e.g., purchases or inventory updates) are reflected across all other windows after refresh.
 * </p>
 *
 * <p>
 * Note: This class does not load persisted data by itself. Any loading of persisted catalog/orders
 * should be performed by the application startup code before creating this window, or inside the
 * {@link StoreEngine}/{@link StoreController} initialization if implemented there.
 * </p>
 */
public class LauncherWindow extends JFrame {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Shared store engine instance used by all windows. */
    private final StoreEngine engine;

    /** Opens a new customer window. */
    private final JButton openCustomerButton = new JButton("Open Customer window");

    /**
     * Opens the manager window.
     * <p>
     * The manager window is managed as a Singleton by {@link StoreWindow#openManagerWindow(StoreController)}:
     * clicking this button multiple times must not create multiple manager windows; instead,
     * the existing manager window is brought to the front.
     * </p>
     */
    private final JButton openManagerButton = new JButton("Open Manager window");

    /** Counter used to generate unique default guest usernames. */
    private final AtomicInteger customerCounter = new AtomicInteger(1);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs the launcher window.
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

    // -------------------------------------------------------------------------
    // Customer flow
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Manager flow (Singleton)
    // -------------------------------------------------------------------------

    /**
     * Opens the manager store window using the Manager GUI Singleton.
     * <p>
     * This method intentionally does <b>not</b> disable the "Open Manager window" button.
     * Instead, it delegates to {@link StoreWindow#openManagerWindow(StoreController)} which ensures:
     * </p>
     * <ul>
     *   <li>No additional manager windows are created while one already exists.</li>
     *   <li>If the manager window is already open, it is brought to the front.</li>
     * </ul>
     */
    private void openManagerWindow() {
        Customer customer = new Customer("Admin", "");
        Manager manager = new Manager("Admin", "");
        StoreController controller = new StoreController(engine, customer, manager);

        StoreWindow.openManagerWindow(controller);
    }
}
