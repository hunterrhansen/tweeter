package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends CountResponse {
    public GetFollowingCountResponse(String message) {
        super(message);
    }

    public GetFollowingCountResponse(int count) {
        super(count);
    }
}
