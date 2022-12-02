package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.handler.PagedServiceHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService extends Service {
    public interface GetStoryObserver extends PagedServiceObserver<Status> {}

    public void getStory(AuthToken authToken, User user, int pageSize, Status lastStatus, GetStoryObserver observer) {
        executeTask(new GetStoryTask(authToken, user, pageSize, lastStatus, new GetStoryHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private static class GetStoryHandler extends PagedServiceHandler<Status> {
        public GetStoryHandler(GetStoryObserver observer) { super(observer); }
    }
}
