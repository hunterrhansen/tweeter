package edu.byu.cs.tweeter.client.model.net;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    private static final String SERVER_URL = "https://2a3in8zs3g.execute-api.us-west-1.amazonaws.com/stage_8";

    private final ClientCommunicator clientCommunicator = new ClientCommunicator(SERVER_URL);

    /**
     * Performs a login and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public LoginResponse login(LoginRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, LoginResponse.class);
    }

    public LogoutResponse logout(LogoutRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, LogoutResponse.class);
    }

    public RegisterResponse register(RegisterRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, RegisterResponse.class);
    }

    public UserResponse getUser(UserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, UserResponse.class);
    }

    public FollowResponse follow(FollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, FollowResponse.class);
    }

    public UnfollowResponse unfollow(UnfollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, UnfollowResponse.class);
    }

    public PostStatusResponse postStatus(PostStatusRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, PostStatusResponse.class);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, IsFollowerResponse.class);
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, GetFollowersResponse.class);
    }

    public GetFollowingResponse getFollowing(GetFollowingRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, GetFollowingResponse.class);
    }

    public GetFeedResponse getFeed(GetFeedRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, GetFeedResponse.class);
    }

    public PagedResponse<Status> getStory(GetStoryRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, GetFeedResponse.class);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, GetFollowersCountResponse.class);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, GetFollowingCountResponse.class);
    }
}
