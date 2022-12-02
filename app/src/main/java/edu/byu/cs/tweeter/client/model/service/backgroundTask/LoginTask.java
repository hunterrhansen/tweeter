package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private static final String LOG_TAG = "LoginTask";
    private static final String URL_PATH = "/user/login";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected void runTask() {
        try {
            LoginResponse response = getServerFacade().login(new LoginRequest(this.username, this.password), URL_PATH);
            if (response.isSuccess()) {
                this.authenticatedUser = response.getUser();
                this.authToken = response.getAuthToken();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (IOException | TweeterRemoteException e) {
            Log.e(LOG_TAG, "Unable to login due to exception: " + e.getMessage());
            sendExceptionMessage(e);
        }
    }
}
