package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.service.UserService;
import static bg.tuvarna.sit.wms.util.ValidationUtils.bindManagedToVisible;
import static bg.tuvarna.sit.wms.util.ValidationUtils.showErrorLabel;
import static bg.tuvarna.sit.wms.util.ValidationUtils.validateComboBox;
import static bg.tuvarna.sit.wms.util.ValidationUtils.validateField;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;
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

  @FXML
  private TextField firstNameField;
  @FXML
  private TextField lastNameField;
  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
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
  private Label confirmPasswordErrorLabel;
  @FXML
  private Label phoneErrorLabel;
  @FXML
  private Label roleErrorLabel;

  private final UserService userService;

  public RegistrationController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Initializes the controller. This method is called after the FXML fields are populated.
   * It binds the visibility of error labels to their managed properties so that space is not
   * reserved in the layout when they are invisible.
   */
  @FXML
  void initialize() {

    bindManagedToVisible(firstNameErrorLabel);
    bindManagedToVisible(lastNameErrorLabel);
    bindManagedToVisible(emailErrorLabel);
    bindManagedToVisible(passwordErrorLabel);
    bindManagedToVisible(confirmPasswordErrorLabel);
    bindManagedToVisible(phoneErrorLabel);
    bindManagedToVisible(roleErrorLabel);
  }

  /**
   * Handles the registration action when the registration button is clicked.
   * Validates user input, collects data into a DTO, and attempts to register the user
   * using the user service.
   */
  @FXML
  void handleRegistration(ActionEvent event) {

    if (!validateInput()) {
      return;
    }

    UserRegistrationDto registrationDto = getUserRegistrationDto();
    try {
      userService.registerUser(registrationDto);
      showAlert(Alert.AlertType.INFORMATION,"Registration Successful", "User has been registered successfully!");
      loadView("/views/home.fxml", event);
    } catch (RegistrationException e) {
      showAlert(Alert.AlertType.ERROR,"Registration Failed", e.getMessage());
    }
  }

  /**
   * Handles back functionality to the Home page.
   *
   * @param event The event that triggered this action.
   */
  @FXML
  void handleBack(ActionEvent event) {
    loadView("/views/home.fxml", event);
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

    return isFieldValid(firstNameField, "^[A-Za-z\\s]+$", firstNameErrorLabel, "First name can contain only letters and spaces.") &&
            isFieldValid(lastNameField, "^[A-Za-z\\s]+$", lastNameErrorLabel, "Last name can contain only letters and spaces.") &&
            isFieldValid(emailField, "\\S+@\\S+\\.\\S+", emailErrorLabel, "The provided email is invalid.") &&
            isFieldValid(passwordField, "^(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$", passwordErrorLabel, "Password should contain at least 8 symbols. At least one upper case letter and one special symbol.") &&
            arePasswordsEqual(passwordField, confirmPasswordField, confirmPasswordErrorLabel, "Passwords must match.") &&
            isFieldValid(phoneField, "^(\\+359|0)\\d{9}$", phoneErrorLabel, "Enter a valid phone number.") &&
            isComboBoxValid(roleBox, roleErrorLabel, "Choosing a role is mandatory.");
  }

  private boolean isFieldValid(TextField field, String regex, Label errorLabel, String errorMessage) {

    boolean isValid = validateField(field, regex);
    showErrorLabel(errorLabel, errorMessage, !isValid);
    return isValid;
  }

  private boolean arePasswordsEqual(PasswordField passwordField, PasswordField confirmPasswordField, Label errorLabel, String errorMessage) {

    boolean areEqual = passwordField.getText().trim().equals(confirmPasswordField.getText().trim());
    showErrorLabel(errorLabel, errorMessage, !areEqual);
    confirmPasswordField.setStyle(areEqual ? "" : "-fx-border-color: red;");
    return areEqual;
  }

  private boolean isComboBoxValid(ComboBox<?> comboBox, Label errorLabel, String errorMessage) {

    boolean isValid = validateComboBox(comboBox);
    showErrorLabel(errorLabel, errorMessage, !isValid);
    return isValid;
  }

}
