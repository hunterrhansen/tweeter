package edu.byu.cs.tweeter.client.net;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class GetFollowersCountTest {
    private static final String URL = "/follow/followers/count";
    private FakeData fakeData = new FakeData();
    private User dummyUser;
    private AuthToken authToken;
    private ServerFacade serverFacade;

    @Before
    public void setup() {
        serverFacade = new ServerFacade();
        authToken = new AuthToken();
        dummyUser = fakeData.getFirstUser();
    }

    @Test
    public void testSuccessfulGetFollowersCount() throws IOException, TweeterRemoteException {
        GetFollowersCountResponse response = serverFacade.getFollowersCount(new GetFollowersCountRequest(dummyUser, authToken), URL);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(20, response.getCount());
    }

    @Test
    public void testFailedGetFollowersCount() {
        Assert.assertThrows(TweeterRemoteException.class, () ->  {
            serverFacade.getFollowersCount(new GetFollowersCountRequest(null, authToken), URL);
        });
    }
}
