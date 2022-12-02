package edu.byu.cs.tweeter.server.service.sqs;

import java.util.List;

public class UpdateFeedSQSRequest {
    private String statusID;
    private String posterAlias;
    private List<String> followers;

    public UpdateFeedSQSRequest(String statusID, String posterAlias, List<String> followers) {
        this.statusID = statusID;
        this.posterAlias = posterAlias;
        this.followers = followers;
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

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}
