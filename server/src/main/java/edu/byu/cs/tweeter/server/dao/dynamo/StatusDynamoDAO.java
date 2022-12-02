package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.model.StatusDBData;
import edu.byu.cs.tweeter.server.service.Service;

public class StatusDynamoDAO extends DynamoDAO implements StatusDAO {
    private static final String STORY_TABLE_NAME = "stories";
    private static final String FEED_TABLE_NAME = "feeds";
    private static final String STATUS_KEY = "status_id";
    private static final String POST_KEY = "post";
    private static final String MENTIONS_KEY = "mentions";
    private static final String URLS_KEY = "urls";
    private static final String DATETIME_KEY = "datetime";
    private static final String USER_KEY = "alias";
    private static final String POSTER_KEY = "poster_alias";

    public StatusDynamoDAO() {
        super();
    }

    @Override
    public void postStatusToStory(String posterAlias, String post, String mentions, String urls,
                                  String datetime, String statusID) throws DAOException {
        try {
            Table table = dynamoDB.getTable(STORY_TABLE_NAME);
            table.putItem(new Item()
                    .withPrimaryKey(USER_KEY, posterAlias, STATUS_KEY, statusID)
                    .withString(POST_KEY, post)
                    .withString(MENTIONS_KEY, mentions)
                    .withString(URLS_KEY, urls)
                    .withString(DATETIME_KEY, datetime)
                    .withString(STATUS_KEY, statusID));
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void postStatusToFeeds(String statusID, List<String> followerAliases, String posterAlias) throws DAOException {
        List<Item> items = new ArrayList<>();
        for (String follower : followerAliases) {
            items.add(new Item().withPrimaryKey(USER_KEY, follower, STATUS_KEY, statusID).withString(POSTER_KEY, posterAlias));
        }
        TableWriteItems feedTableWriteItems = new TableWriteItems(FEED_TABLE_NAME).withItemsToPut(items);

        batchWriteItems(feedTableWriteItems);
    }

    @Override
    public List<String> getFeedStatusInfo(String alias, int limit, String lastStatusID) throws DAOException {
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(USER_KEY + " = :a")
                .withValueMap(new ValueMap()
                        .withString(":a", alias))
                .withScanIndexForward(false)
                .withMaxResultSize(limit);
        if (lastStatusID != null) {
            querySpec.withExclusiveStartKey(new PrimaryKey(USER_KEY, alias, STATUS_KEY, lastStatusID));
        }

        try {
            Table table = dynamoDB.getTable(FEED_TABLE_NAME);
            ItemCollection<QueryOutcome> items = table.query(querySpec);
            if (items == null) return null;
            else {
                return convertResultsToStrings(items, POSTER_KEY, STATUS_KEY);
            }
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public List<StatusDBData> getFeed(List<String> alternatingVals) throws DAOException {
        TableKeysAndAttributes storyTableKeysAndAttributes = new TableKeysAndAttributes(STORY_TABLE_NAME);
        storyTableKeysAndAttributes.addHashAndRangePrimaryKeys(USER_KEY, STATUS_KEY, alternatingVals.toArray(new String[alternatingVals.size()]));

        try {
            BatchGetItemOutcome outcome = dynamoDB.batchGetItem(storyTableKeysAndAttributes);
            List<Item> items = outcome.getTableItems().get(STORY_TABLE_NAME);
            System.out.printf("Outcome of getFeed batchGetItem is %s%n", items == null ? "null" : "not null");
            if (items != null) System.out.printf("Found %d items%n", items.size());
            List<StatusDBData> statusData = convertItemsToStatusData(outcome.getTableItems().get(STORY_TABLE_NAME));
            double retries = 0;
            Map<String, KeysAndAttributes> unprocessedKeys = outcome.getUnprocessedKeys();
            while (unprocessedKeys.size() > 0) {
                retries++;
                if (retries > 8) throw new DAOException("Too many attempts to get feed statuses.");
                expWait(retries);
                outcome = dynamoDB.batchGetItemUnprocessed(unprocessedKeys);
                statusData.addAll(convertItemsToStatusData(outcome.getTableItems().get(STORY_TABLE_NAME)));
                unprocessedKeys = outcome.getUnprocessedKeys();
            }
            System.out.printf("Status data size is %d after %d retries%n", statusData.size(), (int) retries);
            return statusData;
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public List<StatusDBData> getStory(String alias, int limit, Status lastStatus) throws DAOException {
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(USER_KEY + " = :a")
                .withValueMap(new ValueMap()
                        .withString(":a", alias))
                .withScanIndexForward(false)
                .withMaxResultSize(limit);
        if (lastStatus != null) {
            querySpec.withExclusiveStartKey(new PrimaryKey(USER_KEY, alias, STATUS_KEY, lastStatus.getID()));
        }

        try {
            Table table = dynamoDB.getTable(STORY_TABLE_NAME);
            ItemCollection<QueryOutcome> items = table.query(querySpec);
            if (items == null) return null;
            else {
                return convertItemsToStatusData(items);
            }
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    private List<StatusDBData> convertItemsToStatusData(List<Item> items) {
        List<StatusDBData> statusData = new ArrayList<>();
        if (items == null) return statusData;
        for (Item item : items) {
            statusData.add(new StatusDBData(new Status(
                    item.getString(POST_KEY),
                    null,
                    item.getString(DATETIME_KEY),
                    Service.serializeToList(item.getString(URLS_KEY)),
                    Service.serializeToList(item.getString(MENTIONS_KEY)),
                    item.getString(STATUS_KEY)
            ), item.getString(USER_KEY)));
        }
        return statusData;
    }

    private List<StatusDBData> convertItemsToStatusData(ItemCollection<QueryOutcome> results) {
        List<StatusDBData> statusData = new ArrayList<>();
        if (results == null) return statusData;
        Iterator<Item> iterator = results.iterator();
        Item item;
        while (iterator.hasNext()) {
            item = iterator.next();
            statusData.add(new StatusDBData(new Status(
                    item.getString(POST_KEY),
                    null,
                    item.getString(DATETIME_KEY),
                    Service.serializeToList(item.getString(URLS_KEY)),
                    Service.serializeToList(item.getString(MENTIONS_KEY)),
                    item.getString(STATUS_KEY)
            ), item.getString(USER_KEY)));
        }
        return statusData;
    }
}
