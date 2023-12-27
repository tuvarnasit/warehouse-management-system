package bg.tuvarna.sit.wms.exceptions;

/**
 * Exception thrown to indicate a problem during the user registration process.
 */
public class RegistrationException extends Exception {

  public RegistrationException(String message) {
    super(message);
  }

  public RegistrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
