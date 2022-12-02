package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {

    private final FollowService followService;

    public FollowersPresenter(PagedView<User> view) {
        super(view);
        this.followService = new FollowService();
    }

    @Override
    protected void getItems(User user) {
        followService.getFollowers(Cache.getInstance().getCurrUserAuthToken(), user,
                PAGE_SIZE, lastItem, new GetFollowersObserver());
    }

    private class GetFollowersObserver extends PagedObserver implements FollowService.GetFollowersObserver {
        @Override
        protected void displayErrorMessage(String message) {
            view.displayMessage("Failed to get followers: " + message);
        }

        @Override
        protected void displayExceptionMessage(Exception ex) {
            view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
        }
    }
}
