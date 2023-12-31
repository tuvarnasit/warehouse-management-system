package bg.tuvarna.sit.wms.exceptions;

/**
 * Exception thrown to indicate an error during the persistence operations of Review entities.
 */
public class ReviewPersistenceException extends Exception {
  public ReviewPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReviewPersistenceException(String message) {
    super(message);
  }
}
