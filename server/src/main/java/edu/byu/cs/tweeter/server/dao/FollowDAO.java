package edu.byu.cs.tweeter.server.dao;

import java.util.List;

public interface FollowDAO {
    List<String> getFollowers(String targetAlias, int limit, String lastUserAlias) throws DAOException;
    List<String> getFollowees(String targetAlias, int limit, String lastUserAlias) throws DAOException;
    void putFollower(String followeeAlias, String followerAlias) throws DAOException;
    void batchPutFollowers(String followeeAlias, List<String> followerAliases) throws DAOException;
    void deleteFollower(String followeeAlias, String followerAlias) throws DAOException;
    boolean isFollower(String followeeAlias, String followerAlias) throws DAOException;
}
