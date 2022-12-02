package edu.byu.cs.tweeter.server.dao.model;

import edu.byu.cs.tweeter.model.domain.User;

public class UserDBData {
    private final User user;
    private final String hashedPassword;
    private final String salt;

    public UserDBData(User user, String hashedPassword, String salt) {
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
