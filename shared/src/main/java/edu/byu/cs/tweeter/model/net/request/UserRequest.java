package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserRequest implements Request {
    private String username;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private UserRequest() {}

    public UserRequest(String username, AuthToken authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
