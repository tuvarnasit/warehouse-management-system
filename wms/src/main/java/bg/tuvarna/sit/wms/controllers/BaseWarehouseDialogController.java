package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.validation.ValidatingComboBox;
import bg.tuvarna.sit.wms.validation.ValidatingTextField;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseWarehouseDialogController implements DialogController {

  @FXML
  private ValidatingTextField nameField;
  @FXML
  private ValidatingTextField streetField;
  @FXML
  private ValidatingTextField cityField;
  @FXML
  private ValidatingTextField zipCodeField;
  @FXML
  private ValidatingTextField countryField;
  @FXML
  private ValidatingTextField sizeField;
  @FXML
  private ValidatingComboBox<ClimateCondition> climateConditionComboBox;
  @FXML
  private ValidatingTextField storageTypeField;
  @FXML
  private TextArea storageTypeDescArea;

  @Setter
  @Getter
  private Stage dialogStage;
  private final Owner owner;

  public BaseWarehouseDialogController(Owner owner) {
    this.owner = owner;
  }

  @FXML
  public void initialize() {

    nameField.setUp(value -> value.matches("^[a-zA-Z0-9\\-_,\"'*()\\[\\]#;<> ]+$"), "Warehouse name can contain only letters, numbers and basic symbols");
    streetField.setUp(value -> value.matches("^[a-zA-Z0-9 .,'\"#\\-]+$"), "Invalid street format");
    cityField.setUp(value -> value.matches("^[A-Z][A-Za-z\\s-]+$"), "City must start with an uppercase letter");
    zipCodeField.setUp(value -> value.matches("^[0-9A-Za-z\\s-]{3,10}$"), "Invalid zip code format");
    countryField.setUp(value -> value.matches("^[A-Z][A-Za-z\\s']+$"), "Country must start with an uppercase letter");
    sizeField.setUp(value -> value.matches("^[1-9]\\d*(\\.\\d+)?$"), "Size must be a valid number");
    climateConditionComboBox.setUp("Climate condition cannot be empty");
    storageTypeField.setUp(value -> value.matches("^[a-zA-Z0-9\\s.,#()-]+$"), "Invalid storage type format");


    climateConditionComboBox.getItems().setAll(ClimateCondition.values());
  }

  @FXML
  public abstract void onSave();

  @FXML
  public void onCancel() {
    dialogStage.close();
  }

  protected boolean validate() {

    return nameField.getIsValid().get() && streetField.getIsValid().get()
        && cityField.getIsValid().get() && zipCodeField.getIsValid().get()
        && countryField.getIsValid().get() && sizeField.getIsValid().get()
        && climateConditionComboBox.getIsValid().get() && storageTypeField.getIsValid().get();
  }

  protected WarehouseDTO createWarehouseDTO() {

    WarehouseDTO warehouseDTO = new WarehouseDTO();

    warehouseDTO.setName(nameField.getText().trim());
    warehouseDTO.setStreet(streetField.getText().trim());
    warehouseDTO.setCityName(cityField.getText().trim());
    warehouseDTO.setZipCode(zipCodeField.getText().trim());
    warehouseDTO.setCountryName(countryField.getText().trim());
    warehouseDTO.setSize(parseDouble(sizeField.getText().trim()));
    warehouseDTO.setClimateCondition(climateConditionComboBox.getValue());
    warehouseDTO.setStorageType(storageTypeField.getText().trim());
    warehouseDTO.setStorageTypeDescription(storageTypeDescArea.getText().trim());
    warehouseDTO.setOwner(owner);

    return warehouseDTO;
  }

  protected void initializeFields(WarehouseDTO warehouseDTO) {
    nameField.setText(warehouseDTO.getName());
    streetField.setText(warehouseDTO.getStreet());
    cityField.setText(warehouseDTO.getCityName());
    zipCodeField.setText(warehouseDTO.getZipCode());
    countryField.setText(warehouseDTO.getCountryName());
    sizeField.setText(warehouseDTO.getSize().toString());
    climateConditionComboBox.setValue(warehouseDTO.getClimateCondition());
    storageTypeField.setText(warehouseDTO.getStorageType());
    storageTypeDescArea.setText(warehouseDTO.getStorageTypeDescription());
  }

  private static Double parseDouble(String numberAsString) {

    try {
      return Double.parseDouble(numberAsString);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
