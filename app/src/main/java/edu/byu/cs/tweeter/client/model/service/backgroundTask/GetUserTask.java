package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {

    public static final String USER_KEY = "user";
    private static final String LOG_TAG = "GetUserTask";
    private static final String URL_PATH = "/user";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private final String alias;

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(authToken, messageHandler);
        this.alias = alias;
    }

    @Override
    protected void runTask() {
        try {
            UserResponse response = getServerFacade().getUser(new UserRequest(this.alias, this.authToken), URL_PATH);
            if (response.isSuccess()) {
                this.user = response.getUser();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (IOException | TweeterRemoteException e) {
            Log.e(LOG_TAG, "Unable to get user due to exception: " + e.getMessage());
            sendExceptionMessage(e);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }
}
