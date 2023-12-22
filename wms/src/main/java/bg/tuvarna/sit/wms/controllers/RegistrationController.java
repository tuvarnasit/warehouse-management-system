package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.service.PasswordHashingService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.util.JpaUtil;
import static bg.tuvarna.sit.wms.util.ValidationUtils.bindManagedToVisible;
import static bg.tuvarna.sit.wms.util.ValidationUtils.showErrorLabel;
import static bg.tuvarna.sit.wms.util.ValidationUtils.validateComboBox;
import static bg.tuvarna.sit.wms.util.ValidationUtils.validateField;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for handling the user registration process.
 * This class manages user input validation, error display, and submission of user registration data.
 *
 * @author Yavor Chamov
 * @version 1.0.0
 */
public class RegistrationController {

  private final UserService userService = new UserService(
          new UserDao(JpaUtil.getEntityManagerFactory()),
          new PasswordHashingService());

  @FXML
  private TextField firstNameField;
  @FXML
  private TextField lastNameField;
  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private TextField phoneField;
  @FXML
  private ComboBox<String> roleBox;
  @FXML
  private Label firstNameErrorLabel;
  @FXML
  private Label lastNameErrorLabel;
  @FXML
  private Label emailErrorLabel;
  @FXML
  private Label passwordErrorLabel;
  @FXML
  private Label phoneErrorLabel;
  @FXML
  private Label roleErrorLabel;

  /**
   * Initializes the controller. This method is called after the FXML fields are populated.
   * It binds the visibility of error labels to their managed properties so that space is not
   * reserved in the layout when they are invisible.
   */
  @FXML
  protected void initialize() {

    bindManagedToVisible(firstNameErrorLabel);
    bindManagedToVisible(lastNameErrorLabel);
    bindManagedToVisible(emailErrorLabel);
    bindManagedToVisible(passwordErrorLabel);
    bindManagedToVisible(phoneErrorLabel);
    bindManagedToVisible(roleErrorLabel);
  }

  /**
   * Handles the registration action when the registration button is clicked.
   * Validates user input, collects data into a DTO, and attempts to register the user
   * using the user service.
   */
  @FXML
  protected void handleRegistration() {

    if (!validateInput()) {
      return;
    }

    UserRegistrationDto registrationDto = getUserRegistrationDto();
    try {
      userService.registerUser(registrationDto);
      showConfirmationDialog("Registration Successful", "User has been registered successfully!");
    } catch (RegistrationException e) {
      showErrorDialog("Registration Failed", e.getMessage());
    }
  }

  /**
   * Collects user input from form fields and creates a DTO for user registration.
   *
   * @return UserRegistrationDto populated with user input data.
   */
  private UserRegistrationDto getUserRegistrationDto() {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName(firstNameField.getText().trim());
    registrationDto.setLastName(lastNameField.getText().trim());
    registrationDto.setEmail(emailField.getText().trim());
    registrationDto.setPassword(passwordField.getText().trim());
    registrationDto.setPhone(phoneField.getText().trim());
    registrationDto.setRole(roleBox.getValue().trim());

    return registrationDto;
  }

  private boolean validateInput() {

    boolean isFirstNameValid = validateField(firstNameField, "^[A-Za-z\\s]+$");
    showErrorLabel(firstNameErrorLabel, "First name can contain only letters and spaces.", !isFirstNameValid);

    boolean isLastNameValid = validateField(lastNameField, "^[A-Za-z\\s]+$");
    showErrorLabel(lastNameErrorLabel, "Last name can contain only letters and spaces.", !isLastNameValid);

    boolean isEmailValid = validateField(emailField, "\\S+@\\S+\\.\\S+");
    showErrorLabel(emailErrorLabel, "The provided email is invalid.", !isEmailValid);

    boolean isPasswordValid = validateField(passwordField, "^(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");
    showErrorLabel(passwordErrorLabel, "Password should contain at least 8 symbols. At least one upper case letter and one special symbol.", !isPasswordValid);

    boolean isPhoneValid = validateField(phoneField, "^(\\+359|0)\\d{9}$");
    showErrorLabel(phoneErrorLabel, "Enter a valid phone number.", !isPhoneValid);

    boolean isRoleValid = validateComboBox(roleBox);
    showErrorLabel(roleErrorLabel, "Choosing a role is mandatory.", !isRoleValid);

    return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isPhoneValid && isRoleValid;
  }

  /**
   * Shows a confirmation dialog with the specified title and content.
   *
   * @param title   The title of the dialog.
   * @param content The content message to be displayed in the dialog.
   */
  private void showConfirmationDialog(String title, String content) {

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Shows an error dialog with the specified title and content.
   *
   * @param title   The title of the dialog.
   * @param content The content message to be displayed in the dialog.
   */
  private void showErrorDialog(String title, String content) {

    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  @FXML
  protected void handleBack(ActionEvent event) {

    loadView("/views/home.fxml", event);
  }
}
