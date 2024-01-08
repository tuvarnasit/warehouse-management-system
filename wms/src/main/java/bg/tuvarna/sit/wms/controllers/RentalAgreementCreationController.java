package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.RentalAgreementCreationDTO;
import bg.tuvarna.sit.wms.dto.RentalRequestDTO;
import bg.tuvarna.sit.wms.exceptions.RentalAgreementCreationException;
import bg.tuvarna.sit.wms.service.RentalAgreementService;
import bg.tuvarna.sit.wms.validation.ValidatingTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static bg.tuvarna.sit.wms.util.ValidationUtils.validateDatePickers;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;

/**
 * Controller class for handling the creation of a rental agreement through a dialog.
 * This controller manages the user interface components and interacts with the
 * RentalAgreementService to perform the necessary actions.
 */
public class RentalAgreementCreationController implements DialogController {

  @FXML
  private ValidatingTextField tenantNamesField;
  @FXML
  private ValidatingTextField companyNameField;
  @FXML
  private ValidatingTextField companyIdField;
  @FXML
  private ValidatingTextField rentField;
  @FXML
  private DatePicker startDatePicker;
  @FXML
  private DatePicker endDatePicker;

  @Getter
  @Setter
  private Stage dialogStage;
  private final RentalRequestDTO rentalRequestDTO;
  private final RentalAgreementService rentalAgreementService;

  public RentalAgreementCreationController(RentalRequestDTO rentalRequestDTO, RentalAgreementService rentalAgreementService) {
    this.rentalRequestDTO = rentalRequestDTO;
    this.rentalAgreementService = rentalAgreementService;
  }

  /**
   * Initializes the controller by setting up validation rules and populating initial values
   * for the date pickers and the rent field.
   */
  public void initialize() {

    tenantNamesField.setUp(value -> value.matches("^[A-Z][a-z]+ [A-Z][a-z]+$"), "Names must start with an uppercase letter");
    companyNameField.setUp(value -> value.matches("^[A-Za-z0-9 '-./&]+$"), "Invalid company name");
    companyIdField.setUp(value -> value.matches("^(?:\\d{9}|\\d{13})$"), "Company id must be either 9 or 13 digits");
    rentField.setUp(value -> value.matches("^\\d{1,3}(,?\\d{3})*(\\.\\d{1,2})?$"), "Invalid money format");

    startDatePicker.setValue(rentalRequestDTO.getStartDate());
    endDatePicker.setValue(rentalRequestDTO.getEndDate());
    rentField.setText(rentalRequestDTO.getMonthlyRent().toString());
  }


  /**
   * Handles the rent action by validating input, creating a rental agreement DTO,
   * and invoking the service to create the rental agreement.
   */
  public void handleRent() {

    if(!validate()) {
      showAlert(Alert.AlertType.WARNING, "Invalid fields", "Ensure you have correctly filled all fields");
      return;
    }

    try {
      rentalAgreementService.createRentalAgreement(createRentalAgreementDTO());
      showAlert(Alert.AlertType.INFORMATION, "Success", "Rental agreement is created!");
    } catch (RentalAgreementCreationException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to create rental agreement", e.getMessage());
    }
    dialogStage.close();
  }

  /**
   * Closes the dialog without creating a rental agreement.
   */
  public void handleCancel() {
    dialogStage.close();
  }

  private boolean validate() {

    return tenantNamesField.getIsValid().get() && companyNameField.getIsValid().get()
        && companyIdField.getIsValid().get() && rentField.getIsValid().get()
        && validateDatePickers(startDatePicker, endDatePicker);
  }

  private RentalAgreementCreationDTO createRentalAgreementDTO() {

    RentalAgreementCreationDTO dto = new RentalAgreementCreationDTO();

    String[] tenantNames = tenantNamesField.getText().trim().split("\\s+");
    dto.setTenantFirstName(tenantNames[0]);
    dto.setTenantLastName(tenantNames[1]);
    dto.setRentalRequestDTO(rentalRequestDTO);
    dto.setAgentDTO(rentalRequestDTO.getAgentDTO());
    dto.setWarehouseDTO(rentalRequestDTO.getWarehouseDTO());
    dto.setMonthlyRent(new BigDecimal(rentField.getText().trim()));
    dto.setCompanyName(companyNameField.getText().trim());
    dto.setCompanyId(companyIdField.getText().trim());
    dto.setStartDate(startDatePicker.getValue());
    dto.setEndDate(endDatePicker.getValue());

    return dto;
  }
}
