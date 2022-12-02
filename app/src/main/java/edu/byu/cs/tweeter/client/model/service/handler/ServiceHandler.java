package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;

public abstract class ServiceHandler extends Handler {
    protected final ServiceObserver observer;

    public ServiceHandler(ServiceObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    public abstract void handleSuccess(Message msg);

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(GetFeedTask.SUCCESS_KEY);
        if (success) {
            handleSuccess(msg);
        } else if (msg.getData().containsKey(GetFeedTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(GetFeedTask.MESSAGE_KEY);
            observer.handleFailure(message);
        } else if (msg.getData().containsKey(GetFeedTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(GetFeedTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
