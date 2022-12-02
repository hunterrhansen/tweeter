package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedRequest extends PagedRequest<Status> implements Request {
    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private GetFeedRequest() {
        super();
    }

    public GetFeedRequest(User targetUser, AuthToken authToken, int limit, Status lastItem) {
        super(targetUser, authToken, limit, lastItem);
    }
}
