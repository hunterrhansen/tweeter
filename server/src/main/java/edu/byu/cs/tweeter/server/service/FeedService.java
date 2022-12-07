package edu.byu.cs.tweeter.server.service;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDynamoDAO;
import edu.byu.cs.tweeter.server.dao.model.DBStatus;

public class FeedService extends Service {

    private final StatusDAO statusDAO;
    private final UserDAO userDAO;

    @Inject
    public FeedService(StatusDAO statusDAO, UserDAO userDAO) {
        this.statusDAO = statusDAO;
        this.userDAO = userDAO;
    }

    public StatusDAO getStatusDAO() { return this.statusDAO; }

    @Override
    public UserDAO getUserDAO() { return this.userDAO; }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        if (request.getTargetUser() == null || request.getTargetUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new GetFeedResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Getting user feed...");

        List<String> feedAliases;
        try {
            feedAliases = getStatusDAO().getFeedAliases(
                    request.getTargetUser().getAlias(), request.getLimit(), request.getLastItem() != null ? request.getLastItem().getID() : null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get status info from feed table: " + e.getMessage());
        }
        if (feedAliases == null || feedAliases.isEmpty()) return new GetFeedResponse(new ArrayList<>(), false);

        List<DBStatus> statusData;
        try {
            statusData = getStatusDAO().getFeed(feedAliases);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get statuses: " + e.getMessage());
        }
        if (statusData == null || statusData.isEmpty()) throw new RuntimeException("[DB Error] Unable to get statuses: ");

        System.out.println("Got user feed...");

        System.out.println("Getting user info...");

        List<String> userAliases = getAllUniqueUsers(statusData);
        List<User> users;
        try {
            users = getUserDAO().batchGetUsers(userAliases);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get status users: " + e.getMessage());
        }
        statusData.sort(Collections.reverseOrder());

        System.out.println("Got user info...");

        return new GetFeedResponse(extractStatuses(statusData, generateUserMap(users)), statusData.size() == request.getLimit());
    }
}
