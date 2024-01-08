package bg.tuvarna.sit.wms.exceptions;

public class CityDAOException extends Exception {

  public CityDAOException(String message) {
    super(message);
  }

  public CityDAOException(String message, Throwable cause) {
    super(message, cause);
  }
}
