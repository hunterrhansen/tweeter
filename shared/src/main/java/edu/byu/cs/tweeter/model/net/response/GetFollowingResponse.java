package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;

/**
 * A paged response for a {@link GetFollowingRequest}.
 */
public class GetFollowingResponse extends PagedResponse<User> {

    public GetFollowingResponse(String message) {
        super(message);
    }

    public GetFollowingResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message, hasMorePages);
    }

    public GetFollowingResponse(List<User> items, boolean hasMorePages) {
        super(items, hasMorePages);
    }
}
