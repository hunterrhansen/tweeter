package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusRequest implements Request {
    private Status status;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private PostStatusRequest() {}

    public PostStatusRequest(Status status, AuthToken authToken) {
        this.status = status;
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
