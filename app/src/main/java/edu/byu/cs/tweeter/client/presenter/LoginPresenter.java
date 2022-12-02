package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter {

    public interface LoginView extends View {
        void loginUser(User loggedInUser);
    }

    private final UserService userService;

    public LoginPresenter(LoginView view) {
        super(view);
        this.userService = new UserService();
    }

    public void loginUser(String alias, String password) {
        // Send the login request.
        userService.loginUser(alias, password, new LoginObserver());
    }

    public void validateLogin(String alias, String password) throws IllegalArgumentException {
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    private class LoginObserver implements UserService.LoginObserver {
        @Override
        public void handleSuccess(User loggedInUser) {
            ((LoginView) view).loginUser(loggedInUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to login because of exception: " + ex.getMessage());
        }
    }
}
