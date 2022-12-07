package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.model.DBStatus;

public abstract class Service {

    protected long generateDatetime() {
        return new Date().getTime();
    }

    public abstract UserDAO getUserDAO();

    public static String deserializeList(List<String> items) {
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for (String item : items) {
            builder.append(prefix);
            prefix = " , ";
            builder.append(item);
        }
        return builder.toString();
    }

    public static List<String> serializeToList(String string) {
        if (string.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(string.split("\\s,\\s")));
    }

    protected List<String> getAllUniqueUsers(List<DBStatus> statusData) {
        Set<String> uniqueUsers = new HashSet<>();
        for (DBStatus data : statusData) {
            uniqueUsers.add(data.getPosterAlias());
        }
        return new ArrayList<>(uniqueUsers);
    }

    protected Map<String, User> generateUserMap(List<User> users) {
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getAlias(), user);
        }
        return userMap;
    }

    protected List<Status> extractStatuses(List<DBStatus> statusData, Map<String, User> userMap) {
        List<Status> statuses = new ArrayList<>();
        for (DBStatus data : statusData) {
            data.getStatus().setUser(userMap.get(data.getPosterAlias()));
            statuses.add(data.getStatus());
        }
        return statuses;
    }

    protected boolean authenticate(AuthToken token) {
        boolean success;
        try {
            success = getUserDAO().authenticate(token, generateDatetime());
        } catch (DAOException e) {
            throw new RuntimeException("Unable to authenticate: " + e.getMessage());
        }

        try {
            if (!success) {
                getUserDAO().deleteAuthToken(token);
                return false;
            }
        } catch (DAOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
