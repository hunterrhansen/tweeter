package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {
    private boolean isFollower;

    public IsFollowerResponse(String message) {
        super(false, message);
        this.isFollower = false;
    }

    public IsFollowerResponse(boolean isFollower) {
        super(true, null);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return this.isFollower;
    }

    public void setIsFollower(boolean isFollower) {
        this.isFollower = isFollower;
    }
}
