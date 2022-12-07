package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * A DAO for accessing 'follow' data from a dynamodb database.
 */
public class FollowDynamoDAO extends DynamoDAO implements FollowDAO {

    private static final String FOLLOW_TABLE_NAME = "follows";
    private static final String INDEX_NAME = "follow-index";
    private static final String FOLLOWING_KEY = "followee_handle";
    private static final String FOLLOWER_KEY = "follower_handle";

    public FollowDynamoDAO() {
        super();
    }

    public List<String> getFollowers(String targetAlias, int limit, String lastUserAlias) throws DAOException {
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(FOLLOWING_KEY + " = :fa")
                .withValueMap(new ValueMap()
                    .withString(":fa", targetAlias))
                .withScanIndexForward(true)
                .withMaxResultSize(limit);
        if (lastUserAlias != null) {
            querySpec.withExclusiveStartKey(new PrimaryKey(FOLLOWING_KEY, targetAlias, FOLLOWER_KEY, lastUserAlias));
        }

        try {
            Table table = dynamoDB.getTable(FOLLOW_TABLE_NAME);
            ItemCollection<QueryOutcome> items = table.query(querySpec);
            if (items == null) return null;
            return mapResultsToStrings(items, FOLLOWER_KEY);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public List<String> getFollowees(String targetAlias, int limit, String lastUserAlias) throws DAOException {
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(FOLLOWER_KEY + " = :fa")
                .withValueMap(new ValueMap()
                        .withString(":fa", targetAlias))
                .withScanIndexForward(true)
                .withMaxResultSize(limit);
        if (lastUserAlias != null) {
            querySpec.withExclusiveStartKey(new PrimaryKey(FOLLOWER_KEY, targetAlias, FOLLOWING_KEY, lastUserAlias));
        }

        try {
            Table table = dynamoDB.getTable(FOLLOW_TABLE_NAME);
            Index index = table.getIndex(INDEX_NAME);
            ItemCollection<QueryOutcome> items = index.query(querySpec);
            if (items == null) return null;
            return mapResultsToStrings(items, FOLLOWING_KEY);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void putFollower(String followeeAlias, String followerAlias) throws DAOException {
        try {
            Table table = dynamoDB.getTable(FOLLOW_TABLE_NAME);
            table.putItem(new Item()
                    .withPrimaryKey(FOLLOWING_KEY, followeeAlias, FOLLOWER_KEY, followerAlias));
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void batchPutFollowers(String followeeAlias, List<String> followerAliases) throws DAOException {
        List<Item> items = new ArrayList<>();
        for (String followerAlias : followerAliases) {
            items.add(new Item().withPrimaryKey(FOLLOWING_KEY, followeeAlias, FOLLOWER_KEY, followerAlias));
        }
        TableWriteItems followTableWriteItems = new TableWriteItems(FOLLOW_TABLE_NAME).withItemsToPut(items);

        batchWriteItems(followTableWriteItems);
    }

    @Override
    public void deleteFollower(String followeeAlias, String followerAlias) throws DAOException {
        try {
            Table table = dynamoDB.getTable(FOLLOW_TABLE_NAME);
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey(FOLLOWING_KEY, followeeAlias, FOLLOWER_KEY, followerAlias));
            table.deleteItem(deleteItemSpec);
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public boolean isFollower(String followeeAlias, String followerAlias) throws DAOException {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(FOLLOWING_KEY, followeeAlias, FOLLOWER_KEY, followerAlias);
        try {
            Table table = dynamoDB.getTable(FOLLOW_TABLE_NAME);
            Item outcome = table.getItem(spec);
            System.out.printf("The outcome of whether %s is following %s is %s%n", followerAlias, followeeAlias, outcome == null ? "null" : "not null");
            return outcome != null;
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }
}
