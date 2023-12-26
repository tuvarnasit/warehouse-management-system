package bg.tuvarna.sit.wms.exceptions;

/**
 * Exception class for handling errors related to saving credentials.
 * <p>
 * This exception is thrown when there are issues during the process of saving user credentials,
 * such as problems with file access, encryption errors, or other issues related to data persistence.
 */
public class CredentialSavingException extends Exception {

  /**
   * Constructs a new CredentialSavingException with the specified detail message.
   *
   * @param message The detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
   */
  public CredentialSavingException(String message) {
    super(message);
  }

  /**
   * Constructs a new CredentialSavingException with the specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
   * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
   *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public CredentialSavingException(String message, Throwable cause) {
    super(message, cause);
  }
}

