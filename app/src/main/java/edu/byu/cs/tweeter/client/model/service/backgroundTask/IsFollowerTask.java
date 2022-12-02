package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    private static final String LOG_TAG = "IsFollowerTask";
    private static final String URL_PATH = "/follow/is-follower";
    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {
            IsFollowerResponse response = getServerFacade().isFollower(new IsFollowerRequest(this.follower, this.followee, this.authToken), URL_PATH);
            if (response.isSuccess()) {
                this.isFollower = response.getIsFollower();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (IOException | TweeterRemoteException e) {
            Log.e(LOG_TAG, "Unable to know if is user is a follower due to exception: " + e.getMessage());
            sendExceptionMessage(e);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
