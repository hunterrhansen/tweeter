package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private static final String LOG_TAG = "GetFollowersTask";
    private static final String URL_PATH = "/follow/followers";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected PagedResponse<User> getItems() throws IOException, TweeterRemoteException {
        return getServerFacade().getFollowers(new GetFollowersRequest(targetUser, authToken, limit, lastItem), URL_PATH);
    }

    @Override
    protected void logException(Exception ex) {
        Log.e(LOG_TAG, "Failed to get followers due to exception", ex);
    }
}
