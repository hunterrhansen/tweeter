package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.model.DBUser;

/**
 * A DAO Interface for accessing 'user' data from the database.
 */
public interface UserDAO {

    /**
     * Gets the user with the specified alias.
     *
     * @param alias the alias of the user to be returned.
     * @return the user.
     * @throws DAOException if an error occurred in accessing the database.
     */
    DBUser getUser(String alias) throws DAOException;

    /**
     * Gets a batch of users from the database.
     *
     * @param aliases the aliases of the users to be returned.
     * @return the users.
     * @throws DAOException if an error occurred in accessing the database.
     */
    List<User> batchGetUsers(List<String> aliases) throws DAOException;

    /**
     * Adds a user to the database.
     *
     * @param alias the alias of the user to be added.
     * @param hashedPassword the hashed password of the user to be added.
     * @param salt the salt used to hash the password.
     * @param firstName the first name of the user to be added.
     * @param lastName the last name of the user to be added.
     * @param imageURL the URL of the image associated with the user.
     * @param numFollowers the number of followers of the user.
     * @param numFollowing the number of users that the user is following.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void addUser(String alias, String hashedPassword, String salt, String firstName,
                           String lastName, String imageURL, int numFollowers, int numFollowing)
            throws DAOException;

    /**
     * Adds a batch of users to the database.
     *
     * @param users the users to be added.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void batchAddUsers(List<DBUser> users) throws DAOException;

    /**
     * Adds an auth token to the database.
     *
     * @param authToken the auth token to be added.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void putAuthToken(AuthToken authToken) throws DAOException;

    /**
     * Deletes an auth token from the database.
     *
     * @param authToken the auth token to be deleted.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void deleteAuthToken(AuthToken authToken) throws DAOException;

    /**
     * Uploads an image to the database.
     *
     * @param image the image to be uploaded.
     * @param alias the alias of the user associated with the image.
     * @param metadata the metadata of the image.
     * @return the URL of the image.
     * @throws DAOException if an error occurred in accessing the database.
     */
    String uploadImage(ByteArrayInputStream image, String alias, ObjectMetadata metadata) throws DAOException;

    /**
     * Verifies that the provided auth token is valid.
     *
     * @param token the auth token to be verified.
     * @param currentDatetime the current datetime.
     * @return true if the auth token is valid.
     * @throws DAOException if an error occurred in accessing the database.
     */
    boolean authenticate(AuthToken token, long currentDatetime) throws DAOException;

    /**
     * Gets the number of followers of the specified user.
     *
     * @param alias the alias of the user whose number of followers is to be returned.
     * @return the number of followers.
     * @throws DAOException if an error occurred in accessing the database.
     */
    int getFollowersCount(String alias) throws DAOException;

    /**
     * Gets the number of users that the specified user is following.
     *
     * @param alias the alias of the user whose number of users that they are following is to be
     * @return the number of users that the specified user is following.
     * @throws DAOException if an error occurred in accessing the database.
     */
    int getFollowingCount(String alias) throws DAOException;

    /**
     * Adds or subtracts the specified number from the number of followers of the specified user.
     *
     * @param alias the alias of the user whose number of followers is to be updated.
     * @param value the value to be added to the number of followers.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void putFollowerCount(String alias, int value) throws DAOException;

    /**
     * Adds or subtracts the specified number from the number of followees of the specified user.
     *
     * @param alias the alias of the user whose number of users that they are following is to be
     * @param value the value to be added to the number of users that the specified user is following.
     * @throws DAOException if an error occurred in accessing the database.
     */
    void putFollowingCount(String alias, int value) throws DAOException;
}
