package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.entities.Address;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.StorageType;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.service.WarehouseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WarehouseCreationDialogController implements DialogController {

  @FXML
  private TextField addressField;
  @FXML
  private TextField cityField;
  @FXML
  private TextField zipCodeField;
  @FXML
  private TextField countryField;
  @FXML
  private TextField sizeField;
  @FXML
  private ComboBox<ClimateCondition> climateConditionComboBox;
  @FXML
  private TextField storageTypeField;
  @FXML
  private TextArea storageTypeDescArea;

  @Setter
  @Getter
  private Stage dialogStage;
  private final WarehouseService warehouseService = new WarehouseService();
  private final Owner owner;

  public WarehouseCreationDialogController(Owner owner) {
    this.owner = owner;
  }

  @FXML
  public void initialize() {
    climateConditionComboBox.getItems().setAll(ClimateCondition.values());
  }

  @FXML
  public void onSave(ActionEvent actionEvent) {

    Warehouse warehouse = new Warehouse();
    Address address = new Address();
    address.setStreet(addressField.getText());
    City city = new City();
    city.setName(cityField.getText());
    Country country = new Country();
    country.setName(countryField.getText());
    city.setCountry(country);
    address.setCity(city);
    address.setZipCode(zipCodeField.getText());
    warehouse.setAddress(address);
    StorageType storageType = new StorageType();
    storageType.setTypeName(storageTypeField.getText());
    storageType.setDescription(storageTypeDescArea.getText());
    warehouse.setStorageType(storageType);
    warehouse.setSize(Double.valueOf(sizeField.getText()));
    warehouse.setClimateCondition(climateConditionComboBox.getValue());
    warehouse.setOwner(owner);

    //TODO: Add validation and use a DTO
    warehouseService.saveWarehouse(warehouse);
    dialogStage.close();
  }

  @FXML
  public void onCancel(ActionEvent actionEvent) {
    dialogStage.close();
  }
}
