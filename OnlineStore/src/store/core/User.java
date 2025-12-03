/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.core;
import java.util.Objects;


/**
 * Represents a basic user in the store system.
 * This abstract class contains common user information such as
 * username and email. The class is immutable and does not provide
 * any setters, ensuring user identity cannot be modified after creation.
 *
 * Subclasses may represent different types of users such as customers
 * or administrators, and may extend the functionality of this class.
 */
public abstract class User {

    //data members:
    private String username;
    private String email;


    /**
     * Constructs a new User with validation.
     * Invalid or null values are replaced with default values so the
     * user object is always created in a valid and stable state.
     *
     * @param username the username (default used if null or empty)
     * @param email    the email address (default used if null or empty)
     */
    public User(String username, String email) {

        //default values
        this.username = "Unknown user";
        this.email = "unknown@example.com";

        if (username != null && !username.trim().isEmpty()){
            this.username = username;
        }

        if (email != null && !email.trim().isEmpty()){
            this.email = email;
        }

    }

    //-----------------------------------------------------------------------------------------

    /**
     * Returns the username of this user.
     *
     * @return the username string
     */
    public String getUsername() { return this.username; }


    /**
     * Returns the email address of this user.
     *
     * @return the email string
     */
    public String getEmail() { return this.email; }

    //-----------------------------------------------------------------------------------------


    /**
     * Returns a multi-line string describing the user,
     * including username and email.
     *
     * @return string representation of this user
     */
    @Override
    public String toString () {
        return "Username: " + getUsername() + "\n" +
                "Email: " + getEmail();
    }


    /**
     * Compares this user with another object for equality.
     * Users are considered equal if they share the same username.
     *
     * @param o the object to compare with
     * @return true if the users are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof User)) return false;

        User other = (User) o;

        return Objects.equals(this.username, other.username);

    }


}
