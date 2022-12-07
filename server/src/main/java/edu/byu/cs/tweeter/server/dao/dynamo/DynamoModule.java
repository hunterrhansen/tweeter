package edu.byu.cs.tweeter.server.dao.dynamo;

import com.google.inject.AbstractModule;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

/**
 * Bind the DAO interface classes with their DynamoDB implementation classes.
 */
public class DynamoModule extends AbstractModule {
    @Override
    public void configure() {
        bind(FollowDAO.class).to(FollowDynamoDAO.class);
        bind(StatusDAO.class).to(StatusDynamoDAO.class);
        bind(UserDAO.class).to(UserDynamoDAO.class);
    }
}
