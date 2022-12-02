package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    private final StoryService storyService;

    public StoryPresenter(PagedView<Status> view) {
        super(view);
        this.storyService = new StoryService();
    }

    @Override
    public void getItems(User user) {
        storyService.getStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem,
                new GetStoryObserver());
    }

    private class GetStoryObserver extends PagedObserver implements StoryService.GetStoryObserver {
        @Override
        protected void displayErrorMessage(String message) {
            view.displayMessage("Failed to get story: " + message);
        }

        @Override
        protected void displayExceptionMessage(Exception ex) {
            view.displayMessage("Failed to get story because of exception: " + ex.getMessage());
        }
    }
}
