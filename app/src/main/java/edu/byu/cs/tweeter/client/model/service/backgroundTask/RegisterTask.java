package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {

    private static final String LOG_TAG = "RegisterTask";
    private static final String URL_PATH = "/user/register";

    /**
     * The user's first name.
     */
    private final String firstName;
    
    /**
     * The user's last name.
     */
    private final String lastName;

    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private final byte[] image;

    public RegisterTask(String firstName, String lastName, String username, String password,
                        byte[] image, Handler messageHandler) {
        super(messageHandler, username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    protected void runTask() {
        try {
            RegisterResponse response = getServerFacade().register(
                    new RegisterRequest(this.username, this.firstName, this.lastName, this.password, image), URL_PATH);
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
            Log.e(LOG_TAG, "Unable to register due to exception: " + e.getMessage());
            sendExceptionMessage(e);
        }
    }
}
