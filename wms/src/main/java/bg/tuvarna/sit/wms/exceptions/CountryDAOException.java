package bg.tuvarna.sit.wms.exceptions;

public class CountryDAOException extends Exception{

  public CountryDAOException(String message) {
    super(message);
  }

  public CountryDAOException(String message, Throwable cause) {
    super(message, cause);
  }

}
