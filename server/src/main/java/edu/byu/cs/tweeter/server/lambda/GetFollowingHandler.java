package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoModule;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class GetFollowingHandler implements RequestHandler<GetFollowingRequest, GetFollowingResponse> {
    @Override
    public GetFollowingResponse handleRequest(GetFollowingRequest request, Context context) {
        Injector injector = Guice.createInjector(new DynamoModule());
        FollowService followService = injector.getInstance(FollowService.class);
        return followService.getFollowees(request);
    }
}
