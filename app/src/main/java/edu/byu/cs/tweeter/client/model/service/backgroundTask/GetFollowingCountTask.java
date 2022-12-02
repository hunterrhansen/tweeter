package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private static final String LOG_TAG = "GetFollowingCountTask";
    private static final String URL_PATH = "/follow/following/count";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected CountResponse runCountTask() throws IOException, TweeterRemoteException {
        return getServerFacade().getFollowingCount(new GetFollowingCountRequest(targetUser, authToken), URL_PATH);
    }

    @Override
    protected void logException(Exception e) {
        Log.e(LOG_TAG, "Unable to get following count due to exception: ", e);
    }
}
