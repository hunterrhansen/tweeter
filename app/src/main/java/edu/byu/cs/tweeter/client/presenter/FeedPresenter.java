package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {

    private final FeedService feedService;

    public FeedPresenter(PagedView<Status> view) {
        super(view);
        this.feedService = new FeedService();
    }

    @Override
    protected void getItems(User user) {
        feedService.getFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem,
                new GetFeedObserver());
    }

    private class GetFeedObserver extends PagedObserver implements FeedService.GetFeedObserver {
        @Override
        protected void displayErrorMessage(String message) {
            view.displayMessage("Failed to get feed: " + message);
        }

        @Override
        protected void displayExceptionMessage(Exception ex) {
            view.displayMessage("Failed to get feed because of exception: " + ex.getMessage());
        }
    }
}
