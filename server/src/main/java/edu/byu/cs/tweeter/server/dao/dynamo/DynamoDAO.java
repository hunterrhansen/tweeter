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


public class DynamoDAO {
    protected static final String AWS_REGION = "us-west-1";
    protected static final String TOKEN_TABLE_NAME = "auth-tokens";
    protected static final int BASE_TIMEOUT = 5;

    protected final AmazonDynamoDB client;
    protected final DynamoDB dynamoDB;

    public DynamoDAO() {
        client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(AWS_REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    protected void batchWriteItems(TableWriteItems writeItems) throws DAOException {
        try {
            BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(writeItems);
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            double retries = 0;
            while (unprocessedItems.size() > 0) {
                retries++;
                if (retries > 8) throw new DAOException("Too many attempts to put statuses");
                expWait(retries);
                outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
                unprocessedItems = outcome.getUnprocessedItems();
            }
        } catch (AmazonServiceException e) {
            throw new DAOException(e.getMessage());
        }
    }

    protected void expWait(double retries) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (BASE_TIMEOUT * (Math.pow(2, retries))));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    protected List<String> convertResultsToStrings(ItemCollection<QueryOutcome> results, String hashKey) {
        Iterator<Item> iterator = results.iterator();
        Item item = null;
        List<String> strings = new ArrayList<>();
        while (iterator.hasNext()) {
            item = iterator.next();
            strings.add(item.getString(hashKey));
        }
        return strings;
    }

    protected List<String> convertResultsToStrings(ItemCollection<QueryOutcome> results, String hashKey, String sortKey) {
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
}
