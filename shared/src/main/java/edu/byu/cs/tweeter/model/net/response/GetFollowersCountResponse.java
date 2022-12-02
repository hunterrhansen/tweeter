package edu.byu.cs.tweeter.model.net.response;

public class GetFollowersCountResponse extends CountResponse {
    public GetFollowersCountResponse(String message) {
        super(message);
    }

    public GetFollowersCountResponse(int count) {
        super(count);
    }
}
