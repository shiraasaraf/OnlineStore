/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.core;

/**
 * Represents a store manager (administrator).
 * <p>
 * A manager has the same basic identity properties as a {@link User},
 * but is granted administrative permissions by the controller layer.
 * </p>
 */
public class Manager extends User {

    /**
     * Constructs a new manager.
     * Username and email validation are handled by {@link User}.
     *
     * @param username manager username
     * @param email    manager email address
     */
    public Manager(String username, String email) {
        super(username, email);
    }

    /**
     * Returns a human-readable representation of the manager.
     *
     * @return manager string
     */
    @Override
    public String toString() {
        return "Manager\n" + super.toString();
    }
}
