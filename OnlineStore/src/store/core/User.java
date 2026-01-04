/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.core;

import java.util.Objects;

/**
 * Represents a basic user in the store system.
 * <p>
 * A user has a username and an email address.
 * This class is immutable: user identity cannot be changed after creation.
 * </p>
 * <p>
 * Subclasses represent specific user roles such as {@link Customer} or {@link Manager}.
 * </p>
 */
public abstract class User {

    /** User's username (never null or empty). */
    private final String username;

    /** User's email address (never null or empty). */
    private final String email;

    /**
     * Constructs a new user with basic validation.
     * <p>
     * If the provided username or email is null or empty, default values are used.
     * This guarantees the user object is always in a valid state.
     * </p>
     *
     * @param username user's username
     * @param email    user's email address
     */
    public User(String username, String email) {
        this.username = (username != null && !username.trim().isEmpty())
                ? username
                : "Unknown user";

        this.email = (email != null && !email.trim().isEmpty())
                ? email
                : "unknown@example.com";
    }

    /**
     * Returns the username of this user.
     *
     * @return username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address of this user.
     *
     * @return email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns a human-readable description of the user.
     *
     * @return user details string
     */
    @Override
    public String toString() {
        return "Username: " + username + "\n" +
                "Email: " + email;
    }

    /**
     * Users are considered equal if they have the same username.
     *
     * @param o object to compare with
     * @return true if usernames are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return Objects.equals(this.username, other.username);
    }
}
