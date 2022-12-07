package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDynamoDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service {

    private final FollowDAO followDAO;
    private final UserDAO userDAO;

    @Inject
    public FollowService(FollowDAO followDAO, UserDAO userDAO) {
        this.followDAO = followDAO;
        this.userDAO = userDAO;
    }

    /**
     * Returns an instance of {@link FollowDynamoDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    public FollowDAO getFollowDAO() {
        return this.followDAO;
    }

    @Override
    public UserDAO getUserDAO() { return this.userDAO; }

    /**
     * Returns the users that the user specified in the request is getting followed by. Uses the
     * {@link FollowDAO} to get the users.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        if (request.getTargetUser() == null || request.getTargetUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new GetFollowersResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");
        System.out.println("Getting followers...");

        System.out.printf("Last alias: %s%n", request.getLastItem() != null ? request.getLastItem().getAlias() : "null");
        List<String> followerAliases;
        try {
            followerAliases = getFollowDAO().getFollowers(request.getTargetUser().getAlias(),
                    request.getLimit(), request.getLastItem() != null ? request.getLastItem().getAlias() : null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get list of followers: " + e.getMessage());
        }

        System.out.println("Successfully got followers...");

        if (followerAliases == null || followerAliases.isEmpty()) {
            return new GetFollowersResponse(new ArrayList<>(), false);
        }

        System.out.println(followerAliases);

        try {
            return new GetFollowersResponse(getUserDAO().batchGetUsers(followerAliases),
                    followerAliases.size() == request.getLimit());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get users based on followers: " + e.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses the
     * {@link FollowDAO} to get the users.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        if (request.getTargetUser() == null || request.getTargetUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new GetFollowingResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Getting followees...");

        List<String> followeeAliases;
        try {
            followeeAliases = getFollowDAO().getFollowees(request.getTargetUser().getAlias(),
                    request.getLimit(), request.getLastItem() != null ? request.getLastItem().getAlias() : null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get list of followees: " + e.getMessage());
        }

        System.out.println("Successfully got followees...");

        if (followeeAliases == null || followeeAliases.isEmpty()) {
            return new GetFollowingResponse(new ArrayList<>(), false);
        }

        try {
            return new GetFollowingResponse(getUserDAO().batchGetUsers(followeeAliases),
                    followeeAliases.size() == request.getLimit());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get users based on followees: " + e.getMessage());
        }
    }

    /**
     * Follow a user. Uses the {@link FollowDAO} to follow the user.
     *
     * @param request contains information about the user to follow and any other information
     * @return the response object.
     */
    public FollowResponse follow(FollowRequest request) {
        if (request.getLoggedInUser() == null || request.getLoggedInUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a logged in user");
        }
        if (request.getFollowee() == null || request.getFollowee().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authToken");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new FollowResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Following...");

        try {
            getFollowDAO().putFollower(request.getFollowee().getAlias(), request.getLoggedInUser().getAlias());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to add follower: " + e.getMessage());
        }

        System.out.println("Successfully followed...");

        System.out.println("Incrementing following and follower counts...");

        try {
            getUserDAO().putFollowingCount(request.getLoggedInUser().getAlias(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to update following count: " + e.getMessage());
        }

        try {
            getUserDAO().putFollowerCount(request.getFollowee().getAlias(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to update followers count for user \"" +
                request.getFollowee().getAlias() + "\": " + e.getMessage());
        }

        System.out.println("Successfully updated following and follower counts...");

        return new FollowResponse(true);
    }

    /**
     * Unfollow a user. Uses the {@link FollowDAO} to unfollow the user.
     *
     * @param request contains information about the user to unfollow and any other information
     * @return the response object.
     */
    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getLoggedInUser() == null || request.getLoggedInUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a logged in user");
        }
        if (request.getUnfollowee() == null || request.getUnfollowee().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an unfollowee");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authToken");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new UnfollowResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Unfollowing...");

        try {
            getFollowDAO().deleteFollower(request.getUnfollowee().getAlias(), request.getLoggedInUser().getAlias());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to delete follower: " + e.getMessage());
        }

        System.out.println("Successfully unfollowed...");

        System.out.println("Decrementing following and follower counts...");

        try {
            getUserDAO().putFollowingCount(request.getLoggedInUser().getAlias(), -1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to update following count: " + e.getMessage());
        }

        try {
            getUserDAO().putFollowerCount(request.getUnfollowee().getAlias(), -1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to update followers count for user \"" +
                    request.getUnfollowee().getAlias() + "\": " + e.getMessage());
        }

        System.out.println("Successfully updated following and follower counts...");

        return new UnfollowResponse(true);
    }

    /**
     * Checks if a user follows another. Uses the {@link FollowDAO} to check if the user follows the other.
     *
     * @param request contains information about the user to check and any other information
     * @return the response object.
     */
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        else if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authToken");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new IsFollowerResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Checking is follower...");

        try {
            boolean isFollower = getFollowDAO().isFollower(request.getFollowee().getAlias(), request.getFollower().getAlias());
            System.out.println("isFollower is " + isFollower);
            return new IsFollowerResponse(isFollower);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to determine follow relationship between \"" +
                request.getFollowee().getAlias() + "\" and \"" + request.getFollower().getAlias() + "\"");
        }
    }

    public void followTestUser() {
        List<String> followerAliases = new ArrayList<>();
        int currentUserIndex = 0;

        System.out.println("Following test user...");

        for (int i = 0; i < 10000; i += 25) {
            followerAliases.clear();
            for (int j = 0; j < 25; j++) {
                currentUserIndex = i + j;
                followerAliases.add("@test" + currentUserIndex);
            }
            try {
                getFollowDAO().batchPutFollowers("@gghansen", followerAliases);
            } catch (DAOException e) {
                throw new RuntimeException("Unable to batch put followers. Failed at " + i + "-" + (i+25));
            }
        }

        System.out.println("Successfully followed test user...");
    }
}
