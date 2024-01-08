package bg.tuvarna.sit.wms.exceptions;

public class RentalRequestDAOException extends Exception {

  public RentalRequestDAOException(String message) {
    super(message);
  }

  public RentalRequestDAOException(String message, Throwable cause) {
    super(message, cause);
  }
}
