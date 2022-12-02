package edu.byu.cs.tweeter.server.service.sqs;

public class PostStatusSQSRequest {
    private String statusID;
    private String posterAlias;

    public PostStatusSQSRequest() {}

    public PostStatusSQSRequest(String statusID, String posterAlias) {
        this.statusID = statusID;
        this.posterAlias = posterAlias;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getPosterAlias() {
        return posterAlias;
    }

    public void setPosterAlias(String posterAlias) {
        this.posterAlias = posterAlias;
    }
}
