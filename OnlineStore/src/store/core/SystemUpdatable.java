/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */


package store.core;

/**
 * A component that can be periodically updated (e.g., by a background thread).
 */
public interface SystemUpdatable {

    /**
     * Performs a periodic update.
     */
    void update();
}
