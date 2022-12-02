package edu.byu.cs.tweeter.client.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedServiceObserver;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class GetStoryTest {
    private final FakeData fakeData = new FakeData();
    private StoryService storyServiceSpy;
    private StoryServiceObserver observer;
    private AuthToken authToken;
    private User dummyUser;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() {
        dummyUser = fakeData.getFirstUser();
        authToken = new AuthToken();

        storyServiceSpy = Mockito.spy(new StoryService());

        // Setup an observer for the FollowService
        observer = Mockito.mock(StoryServiceObserver.class);

        Mockito.doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(observer).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    /**
     * A {@link StoryService.GetStoryObserver} implementation that can be used to get the values
     * eventually returned by an asynchronous call on the {@link StoryService}. Counts down
     * on the countDownLatch so tests can wait for the background thread to call a method on the
     * observer.
     */
    private class StoryServiceObserver implements StoryService.GetStoryObserver {

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
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

    @Test
    public void testGetStory_validRequest_correctResponse() throws InterruptedException {
        storyServiceSpy.getStory(authToken, dummyUser, 3, null, this.observer);
        awaitCountDownLatch();

        Mockito.verify(observer).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());
    }
}
