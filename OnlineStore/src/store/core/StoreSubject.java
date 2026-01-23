/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.core;

/**
 * Subject role of the Observer pattern.
 * <p>
 * The store engine acts as a {@code StoreSubject}. UI components (windows/panels)
 * can register as observers and will be notified whenever the store model changes
 * (catalog updates, inventory changes, new orders, etc.).
 * </p>
 */
public interface StoreSubject {

    /**
     * Registers an observer.
     *
     * @param observer observer to register (ignored if {@code null})
     */
    void addObserver(SystemUpdatable observer);

    /**
     * Unregisters an observer.
     *
     * @param observer observer to remove (ignored if {@code null})
     */
    void removeObserver(SystemUpdatable observer);

    /**
     * Notifies all currently registered observers that the model has changed.
     */
    void notifyObservers();
}
