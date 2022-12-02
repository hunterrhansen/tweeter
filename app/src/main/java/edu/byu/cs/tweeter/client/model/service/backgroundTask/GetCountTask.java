package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

public abstract class GetCountTask extends AuthenticatedTask {

    public static final String COUNT_KEY = "count";

    /**
     * The user whose count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    protected final User targetUser;

    private int count;

    protected GetCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
    }

    protected User getTargetUser() {
        return targetUser;
    }

    @Override
    protected void runTask() {
        try {
            CountResponse response = runCountTask();
            if (response.isSuccess()) {
                this.count = response.getCount();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException e) {
            logException(e);
            sendExceptionMessage(e);
        }

        // Call sendSuccessMessage if successful
        sendSuccessMessage();
        // or call sendFailedMessage if not successful
        // sendFailedMessage()
    }

    protected abstract CountResponse runCountTask() throws IOException, TweeterRemoteException;
    protected abstract void logException(Exception e);

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, count);
    }
}
