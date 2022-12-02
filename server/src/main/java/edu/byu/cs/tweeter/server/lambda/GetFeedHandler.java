package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoModule;
import edu.byu.cs.tweeter.server.service.FeedService;

public class GetFeedHandler implements RequestHandler<GetFeedRequest, GetFeedResponse> {
    @Override
    public GetFeedResponse handleRequest(GetFeedRequest request, Context context) {
        Injector injector = Guice.createInjector(new DynamoModule());
        FeedService feedService = injector.getInstance(FeedService.class);
        return feedService.getFeed(request);
    }
}
