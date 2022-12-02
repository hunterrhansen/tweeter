package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {

    private static final String LOG_TAG = "FollowTask";
    private static final String URL_PATH = "/follow";

    private final User loggedInUser;
    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(User loggedInUser, AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.loggedInUser = loggedInUser;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {
            FollowResponse response = getServerFacade().follow(new FollowRequest(this.loggedInUser, this.followee, this.authToken), URL_PATH);
            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (IOException | TweeterRemoteException e) {
            Log.e(LOG_TAG, "Unable to follow due to exception: " + e.getMessage());
            sendExceptionMessage(e);
        }
    }

}
