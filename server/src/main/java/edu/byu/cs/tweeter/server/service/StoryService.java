package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDynamoDAO;
import edu.byu.cs.tweeter.server.dao.model.StatusDBData;

public class StoryService extends Service {
    private StatusDAO statusDAO;
    private UserDAO userDAO;

    public StoryService() {}

    @Inject
    public StoryService(StatusDAO statusDAO, UserDAO userDAO) {
        this.statusDAO = statusDAO;
        this.userDAO = userDAO;
    }

    /**
     * Returns an instance of {@link StatusDynamoDAO}. Allows mocking of the StatusDAO class
     * for testing purposes. All usages of StatusDAO should get their StatusDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    public StatusDAO getStatusDAO() { return this.statusDAO; }

    @Override
    public UserDAO getUserDAO() { return this.userDAO; }

    public GetStoryResponse getStory(GetStoryRequest request) {
        if (request.getTargetUser() == null || request.getTargetUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new GetStoryResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Getting user story...");

        List<StatusDBData> statusData;
        try {
            statusData = getStatusDAO().getStory(request.getTargetUser().getAlias(), request.getLimit(), request.getLastItem());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get story statuses: " + e.getMessage());
        }
        if (statusData == null || statusData.isEmpty()) {
            return new GetStoryResponse(new ArrayList<>(), false);
        }
        System.out.printf("Received %d items from story query%n", statusData.size());

        List<String> userAliases = getAllUniqueUsers(statusData);
        List<User> users;
        try {
            users = getUserDAO().batchGetUsers(userAliases);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to get status users: " + e.getMessage());
        }

        System.out.println("Successfully got story...");

        return new GetStoryResponse(extractStatuses(statusData, generateUserMap(users)),
                statusData.size() == request.getLimit());
    }
}
