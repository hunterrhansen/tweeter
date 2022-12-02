package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {

    public interface PagedView<T> extends View {
        void setLoadingStatus(boolean value);
        void addItems(List<T> items);
        void newUserActivity(User user);
    }

    protected static final int PAGE_SIZE = 10;

    protected final UserService userService;
    protected final PagedView<T> view;
    protected boolean hasMorePages;
    protected boolean isLoading = false;
    protected T lastItem;

    public PagedPresenter(PagedView<T> view) {
        super(view);
        this.view = view;
        this.userService = new UserService();
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingStatus(true);
            getItems(user);
        }
    }

    public void getUser(String userAlias) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
        view.displayMessage("Getting user profile...");
    }

    protected abstract void getItems(User user);

    protected abstract class PagedObserver implements PagedServiceObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingStatus(false);
            setHasMorePages(hasMorePages);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingStatus(false);
            displayErrorMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingStatus(false);
            displayExceptionMessage(ex);
        }

        protected abstract void displayErrorMessage(String message);
        protected abstract void displayExceptionMessage(Exception ex);
    }

    private class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void handleSuccess(User user) {
            view.newUserActivity(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }
    }
}
