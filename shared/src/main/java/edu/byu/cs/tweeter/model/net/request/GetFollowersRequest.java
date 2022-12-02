package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersRequest extends PagedRequest<User> implements Request {
    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private GetFollowersRequest() {
        super();
    }

    public GetFollowersRequest(User targetUser, AuthToken authToken, int limit, User lastItem) {
        super(targetUser, authToken, limit, lastItem);
    }
}
