package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.session.UserSession;
import bg.tuvarna.sit.wms.validation.ValidatingComboBox;
import bg.tuvarna.sit.wms.validation.ValidatingTextArea;
import bg.tuvarna.sit.wms.validation.ValidatingTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * An abstract controller class, which contains all of the common methods of {@link WarehouseCreationDialogController}
 * and {@link WarehouseUpdateDialogController} classes such as: initialization for all of the fields and setting a
 * validation function for each field, conversion from DTO to entity, and validation.
 */
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
  private ValidatingTextArea storageTypeDescArea;
  @FXML
  private HBox buttonsWrapper;

  @Setter
  @Getter
  private Stage dialogStage;
  private Owner owner;

  /**
   * This method initializes the the controller and sets up the validation function and tooltip message
   * for every field. Also sets the values in the climate condition combo box.
   */
  @FXML
  public void initialize() {
    owner = getOwnerFromUserSession();

    Button saveButton = new Button("Save");
    Button cancelButton = new Button("Cancel");
    saveButton.setId("save");
    cancelButton.setId("cancel");
    Region region = new Region();
    HBox.setHgrow(region, Priority.ALWAYS);
    saveButton.setOnAction(e -> handleSave());
    cancelButton.setOnAction(e -> handleCancel());
    buttonsWrapper.getChildren().addAll(saveButton, region, cancelButton);

    nameField.setUp(value -> value.matches("^[a-zA-Z0-9\\-_,\"'*()\\[\\]#;<> ]+$"), "Warehouse name can contain only letters, numbers and basic symbols");
    streetField.setUp(value -> value.matches("^[a-zA-Z0-9 .,'\"#\\-]+$"), "Invalid street format");
    cityField.setUp(value -> value.matches("^[A-Z][A-Za-z\\s-]+$"), "City must start with an uppercase letter");
    zipCodeField.setUp(value -> value.matches("^[0-9A-Za-z\\s-]{3,10}$"), "Invalid zip code format");
    countryField.setUp(value -> value.matches("^[A-Z][A-Za-z\\s']+$"), "Country must start with an uppercase letter");
    sizeField.setUp(value -> value.matches("^[1-9]\\d*(\\.\\d+)?$"), "Size must be a valid number");
    climateConditionComboBox.setUp("Climate condition cannot be empty");
    storageTypeField.setUp(value -> value.matches("^[a-zA-Z0-9\\s.,#()-]+$"), "Invalid storage type format");
    storageTypeDescArea.setUp(value -> value.length() <= 400, "Description must be 400 characters maximum");

    climateConditionComboBox.getItems().setAll(ClimateCondition.values());
  }

  /**
   * An abstract method which is triggered when the save button is clicked. It is implemented by
   * {@link WarehouseCreationDialogController#handleSave()} and {@link WarehouseCreationDialogController#handleSave()}
   */
  @FXML
  public abstract void handleSave();

  /**
   * This method is triggered when the cancel button is clicked. It closes the dialog.
   */
  @FXML
  public void handleCancel() {
    dialogStage.close();
  }

  /**
   * Validates the field in the dialog by getting the value of the isValid property of the fields,
   * which is set by the validation function on every change of the text.
   *
   * @return true if all of the fields are valid, false if they aren't
   */
  protected boolean validate() {

    return nameField.getIsValid().get() && streetField.getIsValid().get()
        && cityField.getIsValid().get() && zipCodeField.getIsValid().get()
        && countryField.getIsValid().get() && sizeField.getIsValid().get()
        && climateConditionComboBox.getIsValid().get() && storageTypeField.getIsValid().get();
  }

  /**
   * Creates a WarehouseDTO object from all of the field values in the dialog.
   *
   * @return the warehouseDTO with the data the user has entered
   */
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

  /**
   * Initializes the fields in the dialog with the values from a WarehouseDTO object.
   *
   * @param warehouseDTO the WarehouseDTO object
   */
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

  private Owner getOwnerFromUserSession() {

    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser.getRole().equals(Role.OWNER)) {
      return (Owner) currentUser;
    }
    throw new IllegalStateException("Only owners are allowed to access this operation");
  }
}
