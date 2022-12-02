package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterTest {
    MainPresenter presenter;
    MainPresenter spyPresenter;
    MainPresenter.MainView mockView;
    StatusService mockService;

    @Before
    public void setup() {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        presenter = new MainPresenter(mockView);
        spyPresenter = Mockito.spy(presenter);
        mockService = Mockito.mock(StatusService.class);

        Mockito.when(spyPresenter.getStatusService()).thenReturn(mockService);
    }

    @Test
    public void TestPostSuccessful() throws Exception {
        Mockito.doAnswer(invocation -> {
            ((MainPresenter.PostStatusObserver) invocation.getArgument(5)).handleSuccess();
            return new Object();
        }).when(mockService).postStatus(Mockito.anyString(),
                        Mockito.any(User.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyList(),
                        Mockito.any(MainPresenter.PostStatusObserver.class));

        spyPresenter.postStatus("Some post");

        Mockito.verify(mockView).cancelPostingToast();
    }

    @Test
    public void TestPostError() throws Exception {
        Mockito.doAnswer(invocation -> {
            ((MainPresenter.PostStatusObserver) invocation.getArgument(5)).handleFailure("Test error");
            return new Object();
        }).when(mockService).postStatus(Mockito.anyString(),
                Mockito.any(User.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.any(MainPresenter.PostStatusObserver.class));

        spyPresenter.postStatus("Some post");

        Mockito.verify(mockView).displayMessage("Failed to post status: Test error");
    }

    @Test
    public void TestPostException() throws Exception {
        Mockito.doThrow(new Exception("Test exception")).when(mockService).postStatus(
                Mockito.anyString(),
                Mockito.any(User.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.any(MainPresenter.PostStatusObserver.class));

        spyPresenter.postStatus("Some post");

        Mockito.verify(mockView).displayMessage("Failed to post status because of exception: Test exception");
    }

    @Test
    public void TestPostStatusParams() throws Exception {
        Mockito.doAnswer(invocation -> {
            Assert.assertEquals("Some post", (String) invocation.getArgument(0));
            Assert.assertNotNull(invocation.getArgument(1));
            Assert.assertEquals(User.class, invocation.getArgument(1).getClass());
            Assert.assertNotNull(invocation.getArgument(2));
            Assert.assertEquals(String.class, invocation.getArgument(2).getClass());
            Assert.assertNotNull(invocation.getArgument(3));
            Assert.assertEquals(0, ((List<String>) invocation.getArgument(3)).size());
            Assert.assertNotNull(invocation.getArgument(4));
            Assert.assertEquals(0, ((List<String>) invocation.getArgument(4)).size());
            Assert.assertNotNull(invocation.getArgument(5));
            Assert.assertEquals(MainPresenter.PostStatusObserver.class, invocation.getArgument(5).getClass());
            return new Object();
        }).when(mockService).postStatus(Mockito.anyString(),
                Mockito.any(User.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.any(MainPresenter.PostStatusObserver.class));

        spyPresenter.postStatus("Some post");
    }
}
