package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.exceptions.CredentialSavingException;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.service.CredentialManagerService;
import static bg.tuvarna.sit.wms.util.ValidationUtils.bindManagedToVisible;
import static bg.tuvarna.sit.wms.util.ValidationUtils.showErrorLabel;
import static bg.tuvarna.sit.wms.util.ValidationUtils.validateField;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller class for the login screen.
 * This class handles user interactions within the login view,
 * such as input validation and user authentication.
 */
public class LoginController {

  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private Label emailErrorLabel;
  @FXML
  private Label authenticationErrorLabel;

  private static final Logger LOGGER = LogManager.getLogger(LoginController.class);

  private final UserService userService;

  private final CredentialManagerService credentialManagerService;

  public LoginController(UserService userService, CredentialManagerService credentialManagerService) {
    this.userService = userService;
    this.credentialManagerService = credentialManagerService;
  }

  @FXML
  void initialize() {

    bindManagedToVisible(emailErrorLabel);
  }

  /**
   * Invoked when the user clicks the login button.
   * This method validates the user input and attempts to authenticate the user.
   *
   * @param event The event that triggered this action.
   */
  @FXML
  void handleLogin(ActionEvent event) {

    String email = emailField.getText();
    String password = passwordField.getText();

    if (!validateInput()) {
      return;
    }

    boolean loginSuccessful = userService.login(email, password);

    if (loginSuccessful) {
      try {
        credentialManagerService.saveCredentials(email, password);
        loadView("/views/application.fxml", event);
      } catch (CredentialSavingException e) {
        LOGGER.error("Error saving credentials for auto-login.", e);
        showAlert(Alert.AlertType.WARNING, "Warning",
                "Could not save credentials for auto-login. You may need to login manually next time.");
      }
    } else {
      authenticationErrorLabel.setText("Invalid email or password");
      authenticationErrorLabel.setVisible(true);
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
   * Validates the user input for email and password.
   * Shows error labels if validation fails.
   *
   * @return true if the input is valid, false otherwise.
   */
  private boolean validateInput() {

    boolean isEmailValid = validateField(emailField, "\\S+@\\S+\\.\\S+");
    showErrorLabel(emailErrorLabel, "The provided email is invalid.", !isEmailValid);
    return isEmailValid;
  }
}
