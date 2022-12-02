package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetStoryRequest extends PagedRequest<Status> implements Request {
    private GetStoryRequest() {
        super();
    }

    public GetStoryRequest(User targetUser, AuthToken authToken, int limit, Status lastItem) {
        super(targetUser, authToken, limit, lastItem);
    }
}
