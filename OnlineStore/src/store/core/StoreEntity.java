/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.core;

/**
 * Represents an entity that can be displayed within the store system.
 * Classes implementing this interface must provide a display name and
 * descriptive details suitable for presenting the entity to the user.
 */
public interface StoreEntity {

    /**
     * Returns the display name of this entity.
     * This value is typically a short, user-visible name such as
     * a product title, customer name, or category label.
     *
     * @return a string representing the name shown in the store interface
     */
    String getDisplayName();

    /**
     * Returns detailed information about this entity.
     * The returned string may include additional attributes such as
     * description, price, category, or any other relevant data used
     * to present this entity more fully in the store interface.
     *
     * @return a descriptive string containing extended details about the entity
     */
    String getDisplayDetails();

}
