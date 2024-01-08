package bg.tuvarna.sit.wms.exceptions;

public class RequestCreationException extends Exception {

  public RequestCreationException(String message) {
    super(message);
  }

  public RequestCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
