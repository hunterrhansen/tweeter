package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.model.DBStatus;

/**
 * A DAO Interface for accessing 'status' data from the database.
 */
public interface StatusDAO {

    /**
     * Gets the story of the specified user.
     *
     * @param alias the alias of the user whose story is to be returned.
     * @param limit the maximum number of statuses to return.
     * @param lastStatus the ID of the last status that was returned in the previous request
     * @return the story.
     * @throws DAOException if an error occurred in accessing the database.
     */
    List<DBStatus> getStory(String alias, int limit, Status lastStatus) throws DAOException;

    /**
     * Gets the feed of the specified user.
     *
     * @param aliases the alternating values of the status IDs and the aliases of the users who posted the statuses.
     * @return the feed.
     * @throws DAOException if an error occurred in accessing the database.
     */
    List<DBStatus> getFeed(List<String> aliases) throws DAOException;

    /**
     * Post a status to the specified user's story.
     *
     * @param posterAlias the alias of the user who posted the status.
     * @param post the status to post.
     * @param mentions the aliases of the users mentioned in the status.
     * @param urls the URLs in the status.
     * @param datetime the date and time the status was posted.
     * @param statusID the ID of the status.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void postStatusToStory(String posterAlias, String post, String mentions, String urls, String datetime, String statusID) throws DAOException;

    /**
     * Posts a batch of statuses to a list of followers.
     *
     * @param statusID the ID of the status.
     * @param followerAliases the aliases of the followers.
     * @param posterAlias the alias of the user who posted the status.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void postStatusToFeeds(String statusID, List<String> followerAliases, String posterAlias) throws DAOException;

    /**
     * Gets the information for a feed
     *
     * @param alias the alias of the user whose feed is to be returned.
     * @param limit the maximum number of statuses to return.
     * @param lastStatusID the ID of the last status that was returned in the previous request
     * @return the feed.
     * @throws DAOException if an error occurred in accessing the database.
     */
    List<String> getFeedAliases(String alias, int limit, String lastStatusID) throws DAOException;
}
