package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response {
    private int count;

    public CountResponse(String message) {
        super(false, message);
        this.count = -1;
    }

    public CountResponse(int count) {
        super(true, null);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
