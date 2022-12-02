package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.model.UserDBData;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class UserDynamoDAO extends DynamoDAO implements UserDAO {
    private static final String USER_TABLE_NAME = "users";
    private static final String BUCKET_NAME = "hunter-profile-images";

    private static final String USER_KEY = "alias";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String IMAGE_KEY = "image_url";
    private static final String PASSWORD_KEY = "password";
    private static final String SALT_KEY = "salt";
    private static final String AUTH_KEY = "auth_token";
    private static final String DATETIME_KEY = "datetime";
    private static final String NUM_FOLLOWERS_KEY = "num_followers";
    private static final String NUM_FOLLOWING_KEY = "num_following";

    private static final int AUTH_TOKEN_TIMEOUT_MINUTES = 5;

    private final AmazonS3 s3;

    public UserDynamoDAO() {
        super();
        s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(AWS_REGION)
                .build();
    }

    @Override
    public UserDBData getUser(String alias) throws DAOException {
        Item outcome;
        try {
            Table table = dynamoDB.getTable(USER_TABLE_NAME);
            GetItemSpec spec = new GetItemSpec().withPrimaryKey(USER_KEY, alias);

            outcome = table.getItem(spec);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }

        if (outcome == null) {
            return null;
        }

        String firstName = outcome.getString(FIRST_NAME_KEY);
        String lastName = outcome.getString(LAST_NAME_KEY);
        String imageURL = outcome.getString(IMAGE_KEY);
        String hashedDBPassword = outcome.getString(PASSWORD_KEY);
        String salt = outcome.getString(SALT_KEY);
        if (firstName == null ||
                lastName == null ||
                alias == null ||
                imageURL == null ||
                hashedDBPassword == null ||
                salt == null) {
            throw new DAOException("User improperly stored in db");
        }

        return new UserDBData(new User(firstName, lastName, alias, imageURL), hashedDBPassword, salt);
    }

    @Override
    public List<User> batchGetUsers(List<String> aliases) throws DAOException {
        TableKeysAndAttributes userTableKeysAndAttributes = new TableKeysAndAttributes(USER_TABLE_NAME);
        userTableKeysAndAttributes.addHashOnlyPrimaryKeys(USER_KEY, aliases.toArray(new String[aliases.size()]));

        try {
            BatchGetItemOutcome outcome = dynamoDB.batchGetItem(userTableKeysAndAttributes);
            List<User> users = convertItemsToUsers(outcome.getTableItems().get(USER_TABLE_NAME));
            double retries = 0;
            Map<String, KeysAndAttributes> unprocessedKeys = outcome.getUnprocessedKeys();
            while (unprocessedKeys.size() > 0) {
                retries++;
                if (retries > 8) throw new DAOException("Too many attempts to get users");
                expWait(retries);
                outcome = dynamoDB.batchGetItemUnprocessed(unprocessedKeys);
                users.addAll(convertItemsToUsers(outcome.getTableItems().get(USER_TABLE_NAME)));
                unprocessedKeys = outcome.getUnprocessedKeys();
            }
            return users;
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void putUser(String alias, String hashedPassword, String salt, String firstName,
                        String lastName, String imageURL, int numFollowers, int numFollowing)
            throws DAOException {
        try {
            Table userTable = dynamoDB.getTable(USER_TABLE_NAME);
            userTable.putItem(
                    new Item()
                            .withPrimaryKey(USER_KEY, alias)
                            .withString(PASSWORD_KEY, hashedPassword)
                            .withString(SALT_KEY, salt)
                            .withString(FIRST_NAME_KEY, firstName)
                            .withString(LAST_NAME_KEY, lastName)
                            .withString(IMAGE_KEY, imageURL)
                            .withInt(NUM_FOLLOWERS_KEY, numFollowers)
                            .withInt(NUM_FOLLOWING_KEY, numFollowing));
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void batchPutUsers(List<UserDBData> userDataList) throws DAOException {
        List<Item> items = new ArrayList<>();
        for (UserDBData userData : userDataList) {
            User user = userData.getUser();
            items.add(new Item()
                    .withPrimaryKey(USER_KEY, user.getAlias())
                    .withString(PASSWORD_KEY, userData.getHashedPassword())
                    .withString(SALT_KEY, userData.getSalt())
                    .withString(FIRST_NAME_KEY, user.getFirstName())
                    .withString(LAST_NAME_KEY, user.getLastName())
                    .withString(IMAGE_KEY, user.getImageUrl())
                    .withInt(NUM_FOLLOWERS_KEY, 0)
                    .withInt(NUM_FOLLOWING_KEY, 1));
        }
        TableWriteItems userTableWriteItems = new TableWriteItems(USER_TABLE_NAME).withItemsToPut(items);

        batchWriteItems(userTableWriteItems);
    }

    @Override
    public void putAuthToken(AuthToken authToken) throws DAOException {
        try {
            Table table = dynamoDB.getTable(TOKEN_TABLE_NAME);
            table.putItem(
                    new Item()
                            .withPrimaryKey(AUTH_KEY, authToken.getToken())
                            .withLong(DATETIME_KEY, authToken.getDatetime()));
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) throws DAOException {
        try {
            Table table = dynamoDB.getTable(TOKEN_TABLE_NAME);
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey(AUTH_KEY, authToken.getToken()));
            table.deleteItem(deleteItemSpec);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public String uploadImage(ByteArrayInputStream image, String alias, ObjectMetadata metadata) throws DAOException {
        try {
            s3.putObject(new PutObjectRequest(BUCKET_NAME, alias + ".png", image, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return ((AmazonS3Client) s3).getResourceUrl(BUCKET_NAME, alias + ".png");
        } catch (SdkClientException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public boolean authenticate(AuthToken token, long currentDatetime) throws DAOException {
        Item outcome;
        Table table;
        try {
            table = dynamoDB.getTable(TOKEN_TABLE_NAME);
            GetItemSpec spec = new GetItemSpec().withPrimaryKey(AUTH_KEY, token.getToken());
            outcome = table.getItem(spec);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }

        if (outcome == null) return false;

        long oldDatetime = outcome.getLong(DATETIME_KEY);
        long diffInMillis = currentDatetime - oldDatetime;
        if (TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS) > AUTH_TOKEN_TIMEOUT_MINUTES) return false;

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#dt", DATETIME_KEY);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(AUTH_KEY, token.getToken())
                .withUpdateExpression("set #dt = :val")
                .withNameMap(nameMap)
                .withValueMap(new ValueMap().withLong(":val", currentDatetime));
        try {
            UpdateItemOutcome updateItemOutcome = table.updateItem(updateItemSpec);
            System.out.println("Successfully updated authToken datetime: " + updateItemOutcome.getUpdateItemResult());
        } catch (Exception e) {
            throw new DAOException("Unable to update authToken datetime: " + e.getMessage());
        }

        return true;
    }

    @Override
    public int getFollowersCount(String alias) throws DAOException {
        Item outcome;
        try {
            Table table = dynamoDB.getTable(USER_TABLE_NAME);
            GetItemSpec spec = new GetItemSpec().withPrimaryKey(USER_KEY, alias);

            outcome = table.getItem(spec);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }

        if (outcome == null) {
            return -1;
        }

        try {
            return outcome.getInt(NUM_FOLLOWERS_KEY);
        } catch (NumberFormatException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public int getFollowingCount(String alias) throws DAOException {
        Item outcome;
        try {
            Table table = dynamoDB.getTable(USER_TABLE_NAME);
            GetItemSpec spec = new GetItemSpec().withPrimaryKey(USER_KEY, alias);

            outcome = table.getItem(spec);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }

        if (outcome == null) {
            return -1;
        }

        try {
            return outcome.getInt(NUM_FOLLOWING_KEY);
        } catch (NumberFormatException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void incrementFollowerCount(String alias, Integer val) throws DAOException {
        try {
            Table table = dynamoDB.getTable(USER_TABLE_NAME);

            Map<String, String> expressionAttributeNames = new HashMap<>();
            expressionAttributeNames.put("#f", NUM_FOLLOWERS_KEY);

            Map<String, Object> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":val", val);

            table.updateItem(USER_KEY, alias,
                    "set #f = #f + :val",
                    expressionAttributeNames,
                    expressionAttributeValues);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void incrementFollowingCount(String alias, Integer val) throws DAOException {
        try {
            Table table = dynamoDB.getTable(USER_TABLE_NAME);

            Map<String, String> expressionAttributeNames = new HashMap<>();
            expressionAttributeNames.put("#f", NUM_FOLLOWING_KEY);

            Map<String, Object> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":val", val);

            table.updateItem(USER_KEY, alias,
                    "set #f = #f + :val",
                    expressionAttributeNames,
                    expressionAttributeValues);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    private List<User> convertItemsToUsers(List<Item> items) {
        Set<User> sortedUsers = new TreeSet<>();
        for (Item item : items) {
            sortedUsers.add(new User(
                    item.getString(FIRST_NAME_KEY),
                    item.getString(LAST_NAME_KEY),
                    item.getString(USER_KEY),
                    item.getString(IMAGE_KEY)));
        }
        return new ArrayList<>(sortedUsers);
    }
}
