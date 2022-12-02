package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {

    private final FollowService followService;

    public FollowingPresenter(PagedView<User> view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    protected void getItems(User user) {
        followService.getFollowing(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE,
                lastItem, new GetFollowingObserver());
    }

    private class GetFollowingObserver extends PagedObserver implements FollowService.GetFollowingObserver {
        @Override
        protected void displayErrorMessage(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        protected void displayExceptionMessage(Exception ex) {
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
        }
    }
}
