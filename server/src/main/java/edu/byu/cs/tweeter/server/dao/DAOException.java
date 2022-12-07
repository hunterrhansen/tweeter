package edu.byu.cs.tweeter.server.dao;

/**
 * Exception thrown when an error occurs when accessing the database.
 */
public class DAOException extends Exception {
    public DAOException(String message) {
        super(message);
    }
}
