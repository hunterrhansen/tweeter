package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handler.ServiceHandler;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {
    public interface GetUserObserver extends ServiceObserver { void handleSuccess(User user); }
    public interface LoginObserver extends ServiceObserver { void handleSuccess(User loggedInUser); }
    public interface LogoutObserver extends ServiceObserver { void handleSuccess(); }
    public interface RegisterObserver extends ServiceObserver { void handleSuccess(User registeredUser); }

    public void getUser(AuthToken currUserAuthToken, String userAlias, GetUserObserver getUserObserver) {
        executeTask(new GetUserTask(currUserAuthToken, userAlias, new GetUserHandler(getUserObserver)));
    }

    public void loginUser(String alias, String password, LoginObserver loginObserver) {
        executeTask(new LoginTask(alias, password, new LoginHandler(loginObserver)));
    }

    public void logoutUser(AuthToken authToken, LogoutObserver observer) {
        executeTask(new LogoutTask(authToken, new LogoutHandler(observer)));
    }

    public void registerUser(byte[] imageBytesBase64, String firstName, String lastName, String alias, String password, RegisterObserver observer) {
        executeTask(new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new RegisterHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends ServiceHandler {
        GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccess(Message msg) {
            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            ((GetUserObserver) observer).handleSuccess(user);
        }
    }

    private static class LoginHandler extends ServiceHandler {
        public LoginHandler(LoginObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccess(Message msg) {
            User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((LoginObserver) observer).handleSuccess(loggedInUser);
        }
    }

    private static class LogoutHandler extends ServiceHandler {
        public LogoutHandler(LogoutObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccess(Message msg) { ((LogoutObserver) observer).handleSuccess(); }

    }

    private static class RegisterHandler extends ServiceHandler {
        public RegisterHandler(RegisterObserver observer) {
            super(observer);
        }

        @Override
        public void handleSuccess(Message msg) {
            User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((RegisterObserver) observer).handleSuccess(registeredUser);
        }
    }
}
