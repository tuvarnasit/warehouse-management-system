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

/**
 * This controller class handles the updating process of an existing warehouse. Inherits methods from the
 * {@link BaseWarehouseDialogController} base class.
 */
public class WarehouseUpdateDialogController extends BaseWarehouseDialogController {

  private final WarehouseDAO warehouseDAO = new WarehouseDAO();
  private final CountryService countryService = new CountryService(new CountryDAO());
  private final CityService cityService = new CityService(new CityDAO());
  private final WarehouseService warehouseService = new WarehouseService(warehouseDAO, countryService, cityService);
  private WarehouseDTO warehouseDTO;

  public WarehouseUpdateDialogController(Owner owner, WarehouseDTO warehouseDTO) {
    super(owner);
    this.warehouseDTO = warehouseDTO;
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
    updatedWarehouseDTO.setOwner(warehouseDTO.getOwner());

    if(!validate()){
      return;
    }

    try {
      warehouseService.updateWarehouse(updatedWarehouseDTO);
    } catch (WarehouseServiceException e) {
      DialogUtil.showErrorAlert("Unable to update warehouse data", e.getMessage());
      return;
    }
    getDialogStage().close();
  }
}
