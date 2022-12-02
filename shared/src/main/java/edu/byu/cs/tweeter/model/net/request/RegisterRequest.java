package edu.byu.cs.tweeter.model.net.request;

public class RegisterRequest implements Request {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private byte[] image;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private RegisterRequest() {}

    public RegisterRequest(String username, String firstName, String lastName, String password, byte[] image) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
