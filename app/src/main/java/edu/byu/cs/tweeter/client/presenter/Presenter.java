package edu.byu.cs.tweeter.client.presenter;

public class Presenter {

    public interface View {
        void displayMessage(String message);
    }

    protected final View view;

    public Presenter(View view) { this.view = view; }
}
