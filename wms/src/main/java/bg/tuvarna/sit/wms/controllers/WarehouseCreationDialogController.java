package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseWarehouseDialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.WarehouseService;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * This controller class handles the creation of a new warehouse. Inherits methods from the
 * {@link BaseWarehouseDialogController} base class.
 */
public class WarehouseCreationDialogController extends BaseWarehouseDialogController {

  private final WarehouseService warehouseService;

  public WarehouseCreationDialogController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  /**
   * This method is triggered when the save button is clicked.
   * Creates a new warehouseDTO and validates it before saving it to the database.
   */
  @FXML
  public void onSave() {

    WarehouseDTO warehouseDTO = createWarehouseDTO();

    if(!validate()){
      return;
    }

    try {
      warehouseService.saveWarehouse(warehouseDTO);
    } catch (WarehouseServiceException e) {
      showAlert(Alert.AlertType.ERROR, "Warehouse creation was unsuccessful", e.getMessage());
    }
    getDialogStage().close();
  }
}
