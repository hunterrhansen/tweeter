package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.handler.PagedServiceHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService extends Service {
    public interface GetFeedObserver extends PagedServiceObserver<Status> {}

    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, GetFeedObserver getFeedObserver) {
        executeTask(new GetFeedTask(currUserAuthToken, user, pageSize, lastStatus, new GetFeedHandler(getFeedObserver)));
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private static class GetFeedHandler extends PagedServiceHandler<Status> {
        public GetFeedHandler(GetFeedObserver observer) { super(observer); }
    }
}
