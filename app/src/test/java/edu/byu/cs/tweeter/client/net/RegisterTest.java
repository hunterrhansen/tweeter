package edu.byu.cs.tweeter.client.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class RegisterTest {
    private static final String URL = "/user/register";
    private ServerFacade serverFacade;
    private final String username = "@dummy_user";
    private final String firstName = "dummy";
    private final String lastName = "name";
    private final String password = "dummy_password";
    private final byte[] image = {};

    @Before
    public void setup() {
        serverFacade = new ServerFacade();
    }

    @Test
    public void testSuccessfulRegister() throws IOException, TweeterRemoteException {
        RegisterResponse response = serverFacade.register(new RegisterRequest(username, firstName, lastName, password, image), URL);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotNull(response.getAuthToken());
    }

    @Test
    public void testFailedRegister() {
        Assert.assertThrows(TweeterRemoteException.class, () ->  {
            serverFacade.register(new RegisterRequest(null, firstName, lastName, password, image), URL);
        });
    }
}
