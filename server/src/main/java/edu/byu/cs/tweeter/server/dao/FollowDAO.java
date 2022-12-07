package edu.byu.cs.tweeter.server.dao;

import java.util.List;

/**
 * A DAO Interface for accessing 'following' data from the database.
 */
public interface FollowDAO {

    /**
     * Get the followers of the specified user.
     *
     * @param targetAlias the alias of the user whose followers are to be returned.
     * @param limit the maximum number of followers to return.
     * @param lastUserAlias the alias of the last follower that was returned in the previous request
     * @return the followers.
     * @throws DAOException if an error occurred in accessing the database.
     */
    List<String> getFollowers(String targetAlias, int limit, String lastUserAlias) throws DAOException;

    /**
     * Get the users that the specified user is following.
     *
     * @param targetAlias the alias of the user whose followees are to be returned.
     * @param limit the maximum number of followees to return.
     * @param lastUserAlias the alias of the last followee that was returned in the previous request
     * @return the followees.
     * @throws DAOException if an error occurred in accessing the database.
     */
    List<String> getFollowees(String targetAlias, int limit, String lastUserAlias) throws DAOException;


    /**
     * Adds a follow relationship between two users to the database.
     *
     * @param followeeAlias the alias of the user who is being followed.
     * @param followerAlias the alias of the user who is following the followee.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void putFollower(String followeeAlias, String followerAlias) throws DAOException;

    /**
     * Adds a batch of follow relationships to the database.
     *
     * @param followeeAlias the alias of the user who is being followed.
     * @param followerAliases the aliases of the users who are following the followee.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void batchPutFollowers(String followeeAlias, List<String> followerAliases) throws DAOException;

    /**
     * Delete the follow relationship between two users from the database.
     *
     * @param followeeAlias the alias of the user who is being followed.
     * @param followerAlias the alias of the user who is following the followee.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void deleteFollower(String followeeAlias, String followerAlias) throws DAOException;

    /**
     * Returns whether or not the specified user is following the other specified user.
     *
     * @param followeeAlias the alias of the user who is being followed.
     * @param followerAlias the alias of the user who is following the followee.
     * @return true if the follower is following the followee, false otherwise.
     * @throws DAOException if an error occurred in accessing the database.
     */
    boolean isFollower(String followeeAlias, String followerAlias) throws DAOException;
}
