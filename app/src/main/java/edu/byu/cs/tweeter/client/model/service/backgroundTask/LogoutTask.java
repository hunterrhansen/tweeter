package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    private static final String LOG_TAG = "LogoutTask";
    private static final String URL_PATH = "/user/logout";

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() {
        try {
            LogoutResponse response = getServerFacade().logout(new LogoutRequest(this.authToken), URL_PATH);
            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (IOException | TweeterRemoteException e) {
            Log.e(LOG_TAG, "Unable to logout due to exception: " + e.getMessage());
            sendExceptionMessage(e);
        }
    }
}
