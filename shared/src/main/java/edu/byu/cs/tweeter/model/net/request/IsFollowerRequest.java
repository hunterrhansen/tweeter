package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest implements Request {
    private User follower;
    private User followee;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private IsFollowerRequest() {}

    public IsFollowerRequest(User follower, User followee, AuthToken authToken) {
        this.follower = follower;
        this.followee = followee;
        this.authToken = authToken;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
