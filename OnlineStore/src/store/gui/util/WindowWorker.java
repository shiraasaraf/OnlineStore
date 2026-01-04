/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.gui.util;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility for running tasks off the Swing EDT and updating the UI on the EDT.
 */
public final class WindowWorker {

    private final ExecutorService executor;

    /**
     * Creates a single-thread worker for the owning window.
     *
     * @param threadName name for the created worker thread
     */
    public WindowWorker(String threadName) {
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, threadName);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Runs a task in the background and optionally updates the UI on success.
     *
     * @param task      background task
     * @param onSuccess runs on the Swing EDT after success (may be null)
     * @param onError   runs on the Swing EDT if an error occurs (may be null)
     */
    public void runAsync(Runnable task, Runnable onSuccess, Consumer<Throwable> onError) {
        executor.submit(() -> {
            try {
                task.run();
                if (onSuccess != null) {
                    SwingUtilities.invokeLater(onSuccess);
                }
            } catch (Throwable ex) {
                if (onError != null) {
                    SwingUtilities.invokeLater(() -> onError.accept(ex));
                }
            }
        });
    }

    /**
     * Runs a task that returns a value in the background and passes the result to the UI on success.
     *
     * @param task      background task that returns a value
     * @param onSuccess runs on the Swing EDT with the task result (may be null)
     * @param onError   runs on the Swing EDT if an error occurs (may be null)
     * @param <T>       result type
     */
    public <T> void runAsync(Supplier<T> task, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        executor.submit(() -> {
            try {
                T result = task.get();
                if (onSuccess != null) {
                    SwingUtilities.invokeLater(() -> onSuccess.accept(result));
                }
            } catch (Throwable ex) {
                if (onError != null) {
                    SwingUtilities.invokeLater(() -> onError.accept(ex));
                }
            }
        });
    }

    /**
     * Stops the worker thread.
     */
    public void close() {
        executor.shutdown();
    }
}
