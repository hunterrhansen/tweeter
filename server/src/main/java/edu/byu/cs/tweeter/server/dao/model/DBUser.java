package edu.byu.cs.tweeter.server.dao.model;

import edu.byu.cs.tweeter.model.domain.User;

/**
 * Represents a user in the database.
 */
public class DBUser {

    /**
     * The user.
     */
    private final User user;

    /**
     * The hashed password of the user.
     */
    private final String hashedPassword;

    /**
     * The salt used to hash the password.
     */
    private final String salt;

    public DBUser(User user, String hashedPassword, String salt) {
        this.user = user;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public User getUser() {
        return user;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }
}
