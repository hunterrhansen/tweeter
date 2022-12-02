package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.handler.ServiceHandler;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {

    public interface PostStatusObserver extends ServiceObserver {
        void handleSuccess();
    }

    public void postStatus(String post, User currUser, String dateTime, List<String> urls, List<String> mentions, PostStatusObserver observer) {
        Status newStatus = new Status(post, currUser, dateTime, urls, mentions);
        executeTask(new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusHandler(observer)));
    }

    private static class PostStatusHandler extends ServiceHandler {
        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccess(Message msg) { ((PostStatusObserver) observer).handleSuccess(); }
    }
}
