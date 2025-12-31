package store.gui.util;

import javax.swing.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Runs business tasks on a dedicated single thread per window.
 * UI callbacks are always executed on the Swing EDT.
 */
public final class WindowWorker implements AutoCloseable {

    private final ExecutorService executor;
    private final String threadName;

    public WindowWorker(String threadName) {
        this.threadName = threadName;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName(threadName);
            t.setDaemon(true);
            return t;
        });
    }

    public String getThreadName() {
        return threadName;
    }

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

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
