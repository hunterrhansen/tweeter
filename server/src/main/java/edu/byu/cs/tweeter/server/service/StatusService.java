package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDynamoDAO;
import edu.byu.cs.tweeter.server.service.sqs.PostStatusSQSRequest;
import edu.byu.cs.tweeter.server.service.sqs.UpdateFeedSQSRequest;

public class StatusService extends Service {
    private static final String POST_STATUS_QUEUE_URL = "https://sqs.us-west-1.amazonaws.com/144580955219/post-status-queue";
    private static final String UPDATE_FEED_QUEUE_URL = "https://sqs.us-west-1.amazonaws.com/144580955219/update-feed-queue";

    private final StatusDAO statusDAO;
    private final UserDAO userDAO;
    private final FollowDAO followDAO;

    @Inject
    public StatusService(StatusDAO statusDAO, UserDAO userDAO, FollowDAO followDAO) {
        this.statusDAO = statusDAO;
        this.userDAO = userDAO;
        this.followDAO = followDAO;
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

    public FollowDAO getFollowDAO() { return this.followDAO; }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request requires a status");
        }
        if (request.getStatus().getUser() == null || request.getStatus().getUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request requires a poster");
        }
        else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request requires an authToken");
        }

        System.out.println("Validating auth token...");

        if (!authenticate(request.getAuthToken())) {
            return new PostStatusResponse("Unable to authenticate! Your session may have expired. Please log out and log back in.");
        }

        System.out.println("Valid auth token...");

        System.out.println("Posting status to story...");

        Status status = request.getStatus();
        status.setID(generateDatetime() + " : " + UUID.randomUUID());
        String mentionsString = deserializeList(status.getMentions());
        String urlsString = deserializeList(status.getUrls());
        try {
            getStatusDAO().postStatusToStory(
                    status.getUser().getAlias(),
                    status.getPost(),
                    mentionsString,
                    urlsString,
                    status.getDatetime(),
                    status.getID()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to post status to story: " + e.getMessage());
        }

        System.out.println("Successfully posted to story...");

        System.out.println("Writing sqs messages...");

        Gson gson = new Gson();
        PostStatusSQSRequest sqsRequest = new PostStatusSQSRequest(status.getID(), status.getUser().getAlias());
        String messageBody = gson.toJson(sqsRequest);

        SendMessageResult sendMessageResult = sendSQSMessage(messageBody, POST_STATUS_QUEUE_URL);
        System.out.printf("Successfully posted post status message %s for status %s by poster %s%n",
                sendMessageResult.getMessageId(), status.getID(), status.getUser().getAlias());

        return new PostStatusResponse(true);
    }

    public void postUpdateFeedMessages(String message) {
        System.out.println("Message: " + message);
        Gson gson = new Gson();
        PostStatusSQSRequest postStatusSQSRequest = gson.fromJson(message, PostStatusSQSRequest.class);
        try {
            List<String> followers = getFollowDAO().getFollowers(postStatusSQSRequest.getPosterAlias(), 25, null);
            while (followers != null && !followers.isEmpty()) {
                UpdateFeedSQSRequest updateFeedSQSRequest = new UpdateFeedSQSRequest(postStatusSQSRequest.getStatusID(), postStatusSQSRequest.getPosterAlias(), followers);
                String messageBody = gson.toJson(updateFeedSQSRequest);
                sendSQSMessage(messageBody, UPDATE_FEED_QUEUE_URL);
                followers = getFollowDAO().getFollowers(postStatusSQSRequest.getPosterAlias(), 25, followers.get(followers.size() - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to send message to update feed queue: " + e.getMessage());
        }

    }

    public void updateFeeds(String message) {
        System.out.println("Message: " + message);
        Gson gson = new Gson();
        UpdateFeedSQSRequest updateFeedSQSRequest = gson.fromJson(message, UpdateFeedSQSRequest.class);
        try {
            getStatusDAO().postStatusToFeeds(updateFeedSQSRequest.getStatusID(), updateFeedSQSRequest.getFollowers(), updateFeedSQSRequest.getPosterAlias());
        } catch (DAOException e) {
            e.printStackTrace();
            throw new RuntimeException("[DB Error] Unable to update feeds: " + e.getMessage());
        }
    }

    private SendMessageResult sendSQSMessage(String messageBody, String url) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(url)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        return sqs.sendMessage(send_msg_request);
    }
}
