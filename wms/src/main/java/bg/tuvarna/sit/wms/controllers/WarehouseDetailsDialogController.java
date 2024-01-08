package bg.tuvarna.sit.wms.controllers;


import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.validation.ValidatingComboBox;
import bg.tuvarna.sit.wms.validation.ValidatingTextArea;
import bg.tuvarna.sit.wms.validation.ValidatingTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * Controller class for managing warehouse details.
 * Implements the DialogController interface for dialog handling.
 * Responsible for initializing and populating UI components with warehouse details.
 * Uses ValidatingTextField, ValidatingComboBox, and ValidatingTextArea for input validation.
 */
public class WarehouseDetailsDialogController implements DialogController {

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

  @Getter
  @Setter
  private Stage dialogStage;
  private WarehouseDTO warehouseDTO;

  public WarehouseDetailsDialogController(WarehouseDTO warehouseDTO) {
    this.warehouseDTO = warehouseDTO;
  }

  /**
   * Initializes the controller, sets up UI components, and populates the fields with warehouse data.
   * Disables editing for display-only fields and configures an OK button for closing the dialog.
   */
  @FXML
  public void initialize() {

    Button okButton = new Button();
    okButton.setText("Ok");
    okButton.setOnAction(this::close);
    okButton.setId("save");
    buttonsWrapper.getChildren().setAll(okButton);
    buttonsWrapper.setAlignment(Pos.CENTER);

    initializeFields(warehouseDTO);
    nameField.setEditable(false);
    streetField.setEditable(false);
    cityField.setEditable(false);
    zipCodeField.setEditable(false);
    countryField.setEditable(false);
    sizeField.setEditable(false);
    climateConditionComboBox.setEditable(false);
    storageTypeField.setEditable(false);
    storageTypeDescArea.setEditable(false);
  }

  private void initializeFields(WarehouseDTO warehouseDTO) {

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

  private void close(ActionEvent actionEvent) {

    dialogStage.close();
  }
}
