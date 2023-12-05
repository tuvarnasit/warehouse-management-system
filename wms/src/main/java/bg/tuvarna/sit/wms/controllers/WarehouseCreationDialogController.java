package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.util.DialogUtil;
import javafx.fxml.FXML;

public class WarehouseCreationDialogController extends BaseWarehouseDialogController {

  private final WarehouseService warehouseService = new WarehouseService();

  public WarehouseCreationDialogController(Owner owner) {
    super(owner);
  }

  @FXML
  public void onSave() {

    WarehouseDTO warehouseDTO = createWarehouseDTO();

    if(!validate()){
      return;
    }

    try {
      warehouseService.saveWarehouse(warehouseDTO);
    } catch (WarehouseServiceException e) {
      DialogUtil.showErrorAlert("Warehouse creation was unsuccessful", e.getMessage());
    }
    getDialogStage().close();
  }
}
