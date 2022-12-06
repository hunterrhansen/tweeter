package edu.byu.cs.tweeter.client.net;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

public class LoginTest {
    private static final String URL = "/user/login";
    private ServerFacade serverFacade;
    private final String username = "@gghansen";
    private final String password = "password";

    @Before
    public void setup() {
        serverFacade = new ServerFacade();
    }

    @Test
    public void testSuccessfulLogin() throws IOException, TweeterRemoteException {
        LoginResponse response = serverFacade.login(new LoginRequest(username, password), URL);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotNull(response.getAuthToken());
    }

    @Test
    public void testFailedLogin() {
        Assert.assertThrows(TweeterRemoteException.class, () ->  {
            serverFacade.login(new LoginRequest(null, password), URL);
        });
    }
}
