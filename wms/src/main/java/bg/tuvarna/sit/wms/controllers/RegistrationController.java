package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;


public class RegistrationController {

  private final UserService userService = new UserService();

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

  // This will cause the label to not take up layout space when it's not visible
  @FXML
  public void initialize() {

    bindManagedToVisible(firstNameErrorLabel);
    bindManagedToVisible(lastNameErrorLabel);
    bindManagedToVisible(emailErrorLabel);
    bindManagedToVisible(passwordErrorLabel);
    bindManagedToVisible(phoneErrorLabel);
    bindManagedToVisible(roleErrorLabel);
  }

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

    boolean isPhoneValid = validateField(phoneField, "^(\\+359|0)\\d{8,10}$");
    showErrorLabel(phoneErrorLabel, "Enter a valid phone number.", !isPhoneValid);

    boolean isRoleValid = validateComboBox(roleBox);
    showErrorLabel(roleErrorLabel, "Choosing a role is mandatory.", !isRoleValid);

    return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isPhoneValid && isRoleValid;
  }


  private boolean validateField(TextInputControl field, String validationCriteria) {

    boolean isValid = !field.getText().trim().isEmpty() && field.getText().trim().matches(validationCriteria);
    field.setStyle(isValid ? "" : "-fx-border-color: red;");

    return isValid;
  }

  private boolean validateComboBox(ComboBox<?> comboBox) {

    boolean isValid = comboBox.getValue() != null;
    comboBox.setStyle(isValid ? "" : "-fx-border-color: red;");

    return isValid;
  }

  private void bindManagedToVisible(Label label) {

    label.managedProperty().bind(label.visibleProperty());
  }

  private void showErrorLabel(Label label, String message, boolean show) {

    label.setText(message);
    label.setVisible(show);
  }

  private void showConfirmationDialog(String title, String content) {

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private void showErrorDialog(String title, String content) {

    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

}
