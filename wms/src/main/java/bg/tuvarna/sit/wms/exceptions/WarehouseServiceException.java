package bg.tuvarna.sit.wms.exceptions;

public class WarehouseServiceException extends Exception {

  public WarehouseServiceException(String message) {
    super(message);
  }

  public WarehouseServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
