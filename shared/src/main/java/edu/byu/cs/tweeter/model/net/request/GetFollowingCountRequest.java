package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingCountRequest extends CountRequest implements Request {
    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private GetFollowingCountRequest() {
        super();
    }

    public GetFollowingCountRequest(User targetUser, AuthToken authToken) {
        super(targetUser, authToken);
    }
}
