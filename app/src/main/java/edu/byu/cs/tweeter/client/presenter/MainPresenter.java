package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    public interface MainView extends View {
        void updateFollowButton(boolean isFollower);
        void updateFollow(boolean follow);
        void updateUnfollow(boolean unfollow);
        void logoutUser();
        void cancelPostingToast();
        void updateFollowersCount(int count);
        void updateFollowingCount(int count);
    }

    private final FollowService followService;
    private final UserService userService;
    private final StatusService statusService;

    public MainPresenter(MainView view) {
        super(view);
        followService = new FollowService();
        userService = new UserService();
        statusService = new StatusService();
    }

    public StatusService getStatusService() {
        if (this.statusService == null) return new StatusService();
        return this.statusService;
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(),
                selectedUser, new IsFollowerObserver());
    }

    public void follow(User selectedUser) {
        view.displayMessage("Adding " + selectedUser.getName() + "...");
        followService.follow(Cache.getInstance().getCurrUser(),
                Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver());
    }

    public void unfollow(User selectedUser) {
        view.displayMessage("Removing " + selectedUser.getName() + "...");
        followService.unfollow(Cache.getInstance().getCurrUser(),
                Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver());
    }

    public void logoutUser() {
        userService.logoutUser(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    public void postStatus(String post) {
        PostStatusObserver observer = new PostStatusObserver();
        try {
            getStatusService().postStatus(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post), observer);
        } catch (Exception e) {
            observer.handleException(e);
        }
    }

    public void getFollowersCount(User selectedUser) {
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowersCountObserver());
    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountObserver());
    }

    private List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public class IsFollowerObserver implements FollowService.IsFollowerObserver {
        @Override
        public void handleSuccess(boolean isFollower) {
            ((MainView) view).updateFollowButton(isFollower);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }
    }

    public class FollowObserver implements FollowService.FollowObserver {
        @Override
        public void handleSuccess() {
            ((MainView) view).updateFollow(true);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow: " + message);
            ((MainView) view).updateFollow(false);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
            ((MainView) view).updateFollow(false);
        }
    }

    public class UnfollowObserver implements FollowService.UnfollowObserver {
        @Override
        public void handleSuccess() {
            ((MainView) view).updateUnfollow(true);
        }

        @Override
        public void handleFailure(String message) {
            ((MainView) view).updateUnfollow(false);
            view.displayMessage("Failed to unfollow: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            ((MainView) view).updateUnfollow(false);
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
        }
    }

    public class LogoutObserver implements UserService.LogoutObserver {
        @Override
        public void handleSuccess() {
            Cache.getInstance().clearCache();
            ((MainView) view).logoutUser();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
        }
    }

    public class PostStatusObserver implements StatusService.PostStatusObserver {
        @Override
        public void handleSuccess() {
            ((MainView) view).cancelPostingToast();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }
    }

    public class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver {
        @Override
        public void handleSuccess(int count) {
            ((MainView) view).updateFollowersCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get followers count because of exception: " + ex.getMessage());
        }
    }

    public class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver {
        @Override
        public void handleSuccess(int count) {
            ((MainView) view).updateFollowingCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }
    }
}
