/**
 * Represents a store manager (administrator) in the store system.
 * A manager is allowed to perform administrative actions such as
 * loading and saving products from/to files.
 */
package store.core;

public class Manager extends User {

    // אפשר לשים שדות נוספים בהמשך (למשל role, employeeId וכו')
    // כרגע אין צורך - רק ההבחנה בין מנהל ללקוח

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

    @Override
    public String toString() {
        return "Manager\n" + super.toString();
    }
}
