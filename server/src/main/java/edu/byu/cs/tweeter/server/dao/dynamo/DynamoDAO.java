package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.byu.cs.tweeter.server.dao.DAOException;

/**
 * An abstract DAO parent class for accessing data from a dynamodb database.
 */
public abstract class DynamoDAO {

    protected static final String AWS_REGION = "us-west-1";
    protected static final String TOKEN_TABLE_NAME = "auth-tokens";

    protected static final int BASE_TIMEOUT = 5;
    protected static final int TOO_MANY_REQUESTS = 8;

    protected final AmazonDynamoDB client = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(AWS_REGION)
                .build();

    protected final DynamoDB dynamoDB = new DynamoDB(client);

    /**
     * Write a list of items to a table in a dynamodb database.
     *
     * @param writeItems the items to write
     * @throws DAOException if an error occurs while writing the items
     */
    protected void batchWriteItems(TableWriteItems writeItems) throws DAOException {
        try {
            BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(writeItems);
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            double retries = 0;
            while (unprocessedItems.size() > 0) {
                retries++;
                if (retries > TOO_MANY_REQUESTS) {
                    throw new DAOException("Too many attempts to put statuses in database");
                }
                expWait(retries);
                outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
                unprocessedItems = outcome.getUnprocessedItems();
            }
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    /**
     * Sleep for a time based on the number of retries.
     *
     * @param retries the number of retries.
     */
    protected void expWait(double retries) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (BASE_TIMEOUT * (Math.pow(2, retries))));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Map the results of a query to a list of strings.
     *
     * @param results the results of a query
     * @param hashKey the name of the hash key
     * @param sortKey the name of the sort key
     * @return a list of strings
     */
    protected List<String> mapResultsToStrings(ItemCollection<QueryOutcome> results, String hashKey, String sortKey) {
        Iterator<Item> iterator = results.iterator();
        Item item;
        List<String> strings = new ArrayList<>();
        while (iterator.hasNext()) {
            item = iterator.next();
            strings.add(item.getString(hashKey));
            strings.add(item.getString(sortKey));
        }
        return strings;
    }

    /**
     * Map the results of a query to a list of strings.
     *
     * @param results the results of a query
     * @param hashKey the name of the hash key
     * @return a list of strings
     */
    protected List<String> mapResultsToStrings(ItemCollection<QueryOutcome> results, String hashKey) {
        Iterator<Item> iterator = results.iterator();
        Item item;
        List<String> strings = new ArrayList<>();
        while (iterator.hasNext()) {
            item = iterator.next();
            strings.add(item.getString(hashKey));
        }
        return strings;
    }
}
