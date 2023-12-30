package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseWarehouseDialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.WarehouseService;
import javafx.scene.control.Alert;

import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;

/**
 * This controller class handles the updating process of an existing warehouse. Inherits methods from the
 * {@link BaseWarehouseDialogController} base class.
 */
public class WarehouseUpdateDialogController extends BaseWarehouseDialogController {

  private final WarehouseService warehouseService;
  private WarehouseDTO warehouseDTO;

  public WarehouseUpdateDialogController(WarehouseDTO warehouseDTO, WarehouseService warehouseService) {
    this.warehouseDTO = warehouseDTO;
    this.warehouseService = warehouseService;
  }

  /**
   * Initializes the text fields by setting the text properties to the values of the warehouseDTO, which is
   * passed to the class.
   */
  @Override
  public void initialize() {
    super.initialize();
    initializeFields(warehouseDTO);
  }

  /**
   * Triggered when the save button is clicked.
   * This method creates a new dto of an existing warehouse, from the text in the fields.
   * It then validates the dto and updates the exising warehouse with the new data.
   */
  @Override
  public void onSave() {

    WarehouseDTO updatedWarehouseDTO = createWarehouseDTO();
    updatedWarehouseDTO.setId(warehouseDTO.getId());
    updatedWarehouseDTO.setStatus(warehouseDTO.getStatus());

    if(!validate()){
      return;
    }

    try {
      warehouseService.updateWarehouse(updatedWarehouseDTO);
    } catch (WarehouseServiceException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to update warehouse data", e.getMessage());
      return;
    }
    getDialogStage().close();
  }
}
