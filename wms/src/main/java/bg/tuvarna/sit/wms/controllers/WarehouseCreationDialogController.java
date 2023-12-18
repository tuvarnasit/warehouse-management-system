package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.dao.WarehouseDAO;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.CityService;
import bg.tuvarna.sit.wms.service.CountryService;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.util.DialogUtil;
import javafx.fxml.FXML;

/**
 * This controller class handles the creation of a new warehouse. Inherits methods from the
 * {@link BaseWarehouseDialogController} base class.
 */
public class WarehouseCreationDialogController extends BaseWarehouseDialogController {

  private final WarehouseDAO warehouseDAO = new WarehouseDAO();
  private final CountryService countryService = new CountryService(new CountryDAO());
  private final CityService cityService = new CityService(new CityDAO());
  private final WarehouseService warehouseService = new WarehouseService(warehouseDAO, countryService, cityService);

  public WarehouseCreationDialogController(Owner owner) {
    super(owner);
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
      DialogUtil.showErrorAlert("Warehouse creation was unsuccessful", e.getMessage());
    }
    getDialogStage().close();
  }
}
