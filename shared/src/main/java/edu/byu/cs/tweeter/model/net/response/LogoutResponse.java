package edu.byu.cs.tweeter.model.net.response;

public class LogoutResponse extends Response {
    public LogoutResponse(String message) {
        super(false, message);
    }

    public LogoutResponse(boolean success) {
        super(success, null);
    }
}
