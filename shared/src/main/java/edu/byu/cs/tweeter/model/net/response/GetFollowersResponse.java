package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersResponse extends PagedResponse<User> {
    public GetFollowersResponse(String message) {
        super(message);
    }

    public GetFollowersResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message, hasMorePages);
    }

    public GetFollowersResponse(List<User> items, boolean hasMorePages) {
        super(items, hasMorePages);
    }
}
