package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class CountRequest implements Request {
    private User targetUser;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Protected so it won't be called in normal code.
     */
    protected CountRequest() {}

    public CountRequest(User targetUser, AuthToken authToken) {
        this.targetUser = targetUser;
        this.authToken = authToken;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
