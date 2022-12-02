package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter {

    public interface RegisterView extends View {
        void registerUser(User registeredUser);
    }

    private final UserService userService;

    public RegisterPresenter(RegisterView view) {
        super(view);
        this.userService = new UserService();
    }

    public void registerUser(Bitmap image, String firstName, String lastName, String alias, String password) {
        userService.registerUser(processImage(image), firstName, lastName, alias, password, new RegisterObserver());
    }

    public void validateRegistration(String firstName, String lastName, String alias,
                                     String password, Drawable imageToUpload) throws IllegalArgumentException {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    private byte[] processImage(Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    private class RegisterObserver implements UserService.RegisterObserver {
        @Override
        public void handleSuccess(User registeredUser) { ((RegisterView) view).registerUser(registeredUser); }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to register: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to register because of exception: " + ex.getMessage());
        }
    }
}
