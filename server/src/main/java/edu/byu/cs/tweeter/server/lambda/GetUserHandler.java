package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoModule;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetUserHandler implements RequestHandler<UserRequest, UserResponse> {
    @Override
    public UserResponse handleRequest(UserRequest userRequest, Context context) {
        Injector injector = Guice.createInjector(new DynamoModule());
        UserService userService = injector.getInstance(UserService.class);
        return userService.getUser(userRequest);
    }
}
