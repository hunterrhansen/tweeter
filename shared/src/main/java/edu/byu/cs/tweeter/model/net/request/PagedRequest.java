package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedRequest<T> implements Request {
    protected User targetUser;
    protected AuthToken authToken;
    protected int limit;
    protected T lastItem;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    protected PagedRequest() {}

    public PagedRequest(User targetUser, AuthToken authToken, int limit, T lastItem) {
        this.targetUser = targetUser;
        this.authToken = authToken;
        this.limit = limit;
        this.lastItem = lastItem;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }
}
