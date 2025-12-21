/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */


package store.core;

/**
 * Represents a store manager (administrator) in the store system.
 * A manager is allowed to perform administrative actions such as
 * loading and saving products from/to files.
 */
public class Manager extends User {


    /**
     * Constructs a new Manager.
     * Username and email validation is handled by the {@link User} constructor.
     *
     * @param username manager username
     * @param email    manager email
     */
    public Manager(String username, String email) {
        super(username, email);
    }

    /**
     * Returns a string representation of the manager.
     *
     * @return manager details
     */
    @Override
    public String toString() {
        return "Manager\n" + super.toString();
    }
}
