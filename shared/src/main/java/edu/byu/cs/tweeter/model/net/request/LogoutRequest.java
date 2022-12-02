package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutRequest implements Request {

    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LogoutRequest() {}

    public LogoutRequest(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
