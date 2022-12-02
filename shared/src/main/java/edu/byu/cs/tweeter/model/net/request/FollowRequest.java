package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest implements Request {
    private User loggedInUser;
    private User followee;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowRequest() {}

    public FollowRequest(User loggedInUser, User followee, AuthToken authToken) {
        this.loggedInUser = loggedInUser;
        this.followee = followee;
        this.authToken = authToken;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }
}
