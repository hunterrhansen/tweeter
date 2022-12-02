package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followees for a specified follower.
 */
public class GetFollowingRequest extends PagedRequest<User> implements Request {
    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private GetFollowingRequest() {
        super();
    }

    public GetFollowingRequest(User targetUser, AuthToken authToken, int limit, User lastItem) {
        super(targetUser, authToken, limit, lastItem);
    }
}
