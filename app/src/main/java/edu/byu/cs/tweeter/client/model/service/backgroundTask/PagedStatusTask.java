package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedStatusTask extends PagedTask<Status> {

    protected PagedStatusTask(AuthToken authToken, User targetUser, int limit, Status lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }
}
