package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.model.StatusDBData;

public interface StatusDAO {
    void postStatusToStory(String posterAlias, String post, String mentions, String urls, String datetime, String statusID) throws DAOException;
    void postStatusToFeeds(String statusID, List<String> followerAliases, String posterAlias) throws DAOException;
    List<String> getFeedStatusInfo(String alias, int limit, String lastStatusID) throws DAOException;
    List<StatusDBData> getFeed(List<String> alternatingVals) throws DAOException;
    List<StatusDBData> getStory(String alias, int limit, Status lastStatus) throws DAOException;
}
