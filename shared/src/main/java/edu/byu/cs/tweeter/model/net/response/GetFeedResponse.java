package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedResponse extends PagedResponse<Status> {
    public GetFeedResponse(String message) {
        super(message);
    }

    public GetFeedResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message, hasMorePages);
    }

    public GetFeedResponse(List<Status> items, boolean hasMorePages) {
        super(items, hasMorePages);
    }
}
