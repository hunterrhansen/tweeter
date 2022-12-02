package edu.byu.cs.tweeter.client.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class TestPostStatus {
    private CountDownLatch countDownLatch;
    private UserServiceObserver userServiceObserver;
    private User loggedInUser;
    private List<Status> statuses;
    private MainPresenter.MainView mockMainView;
    private MainPresenter mainPresenterSpy;
    private StoryServiceObserver storyServiceObserver;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.MainView.class);
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));
        userServiceObserver = Mockito.mock(UserServiceObserver.class);
        storyServiceObserver = Mockito.mock(StoryServiceObserver.class);

        Mockito.doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(mockMainView).cancelPostingToast();

        Mockito.doAnswer(invocation -> {
            loggedInUser = invocation.getArgument(0);
            countDownLatch.countDown();
            return null;
        }).when(userServiceObserver).handleSuccess(Mockito.any());

        Mockito.doAnswer(invocation -> {
            statuses = invocation.getArgument(0);
            countDownLatch.countDown();
            return null;
        }).when(storyServiceObserver).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());

        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class UserServiceObserver implements UserService.LoginObserver {

        @Override
        public void handleSuccess(User user) {
            loggedInUser = user;
            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            countDownLatch.countDown();
        }
    }

    private class StoryServiceObserver implements StoryService.GetStoryObserver {

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {}

        @Override
        public void handleFailure(String message) {}

        @Override
        public void handleException(Exception exception) {}
    }

    @Test
    public void testPostStatus() throws InterruptedException {
        String randomString = UUID.randomUUID().toString();

        UserService userService = new UserService();
        userService.loginUser("@test", "test", userServiceObserver);
        awaitCountDownLatch();
        Mockito.verify(userServiceObserver).handleSuccess(Mockito.any());

        mainPresenterSpy.postStatus(randomString);
        awaitCountDownLatch();
        Mockito.verify(mockMainView).cancelPostingToast();

        StoryService storyService = new StoryService();
        storyService.getStory(Cache.getInstance().getCurrUserAuthToken(), loggedInUser,
                10, null, storyServiceObserver);
        awaitCountDownLatch();
        Mockito.verify(storyServiceObserver).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());

        boolean foundMatch = false;
        for (Status status : statuses) {
            if (status.getPost().equals(randomString)) {
                foundMatch = true;
                break;
            }
        }

        Assert.assertTrue(foundMatch);
    }
}
