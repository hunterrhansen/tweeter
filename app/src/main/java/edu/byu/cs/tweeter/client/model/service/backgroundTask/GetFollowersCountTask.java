package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private static final String LOG_TAG = "GetFollowersCountTask";
    private static final String URL_PATH = "/follow/followers/count";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected CountResponse runCountTask() throws IOException, TweeterRemoteException {
        return getServerFacade().getFollowersCount(new GetFollowersCountRequest(targetUser, authToken), URL_PATH);
    }

    @Override
    protected void logException(Exception e) {
        Log.e(LOG_TAG, "Unable to get followers count due to exception: ", e);
    }
}
