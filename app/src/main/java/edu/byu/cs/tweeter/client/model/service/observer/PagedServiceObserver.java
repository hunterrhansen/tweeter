package edu.byu.cs.tweeter.client.model.service.observer;

import java.util.List;

public interface PagedServiceObserver<T> extends ServiceObserver {
    void handleSuccess(List<T> items, boolean hasMorePages);
}
