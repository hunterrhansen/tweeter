package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedUserTask extends PagedTask<User> {
    protected PagedUserTask(AuthToken authToken, User targetUser, int limit, User lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }
}
