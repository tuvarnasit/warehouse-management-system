package bg.tuvarna.sit.wms.exceptions;

public class CityCreationException extends Exception{

  public CityCreationException(String message) {
    super(message);
  }

  public CityCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
