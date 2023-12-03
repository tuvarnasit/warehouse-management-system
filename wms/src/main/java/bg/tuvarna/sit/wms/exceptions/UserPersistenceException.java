package bg.tuvarna.sit.wms.exceptions;

/**
 * Exception thrown to indicate an error during the persistence operations of User entities.
 */
public class UserPersistenceException extends Exception {
    public UserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}