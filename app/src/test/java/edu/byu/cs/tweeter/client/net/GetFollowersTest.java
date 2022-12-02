package edu.byu.cs.tweeter.client.net;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class GetFollowersTest {
    private static final String URL = "/follow/followers";
    private ServerFacade serverFacade;
    private FakeData fakeData = new FakeData();
    private User user;
    private final int limit = 20;
    private AuthToken authToken;

    @Before
    public void setup() {
        user = fakeData.getFirstUser();
        serverFacade = new ServerFacade();
        authToken = new AuthToken();
    }

    @Test
    public void testSuccessfulGetFollowers() throws IOException, TweeterRemoteException {
        GetFollowersResponse response = serverFacade.getFollowers(new GetFollowersRequest(user, authToken, limit, null), URL);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotNull(response.getItems());
        Assert.assertEquals(limit, response.getItems().size());
    }

    @Test
    public void testInvalidLimitGetFollowers() {
        Assert.assertThrows(TweeterRemoteException.class, () ->  {
            serverFacade.getFollowers(new GetFollowersRequest(user, authToken, -1, null), URL);
        });
    }
}
