package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.server.dao.dynamo.DynamoModule;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostUpdateFeedMessagesHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Injector injector = Guice.createInjector(new DynamoModule());
        StatusService statusService = injector.getInstance(StatusService.class);
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            statusService.postUpdateFeedMessages(msg.getBody());
        }
        return null;
    }
}
