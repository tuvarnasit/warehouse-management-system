package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.service.CredentialManagerService;
import bg.tuvarna.sit.wms.session.Credentials;
import bg.tuvarna.sit.wms.session.UserSession;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Controller class for the home view in the application.
 * It manages the interaction and behavior of the UI elements on the home screen.
 */
public class HomeController {

  @FXML
  Button registerButton;
  @FXML
  Button loginButton;
  @FXML
  Button ssoButton;
  @FXML
  Button logoutButton;
  @FXML
  Text welcomeUserText;
  @FXML
  StackPane welcomeMessageContainer;
  UserSession userSession = UserSession.getInstance();
  private final UserService userService;
  private final CredentialManagerService credentialManagerService;

  public HomeController(UserService userService, CredentialManagerService credentialManagerService) {
    this.userService = userService;
    this.credentialManagerService = credentialManagerService;
  }

  /**
   * Initializes the controller. This method is called after the FXML fields are populated.
   * It sets up the initial state of the UI components based on the user's login status.
   */
  @FXML
  void initialize() {

    updateRegisterButtonVisibility();
    updateLoginButtonVisibility();
    updateSsoButtonVisibility();
    updateLogoutButtonVisibility();
    updateUserWelcomeMessage();
  }

  /**
   * Handles the action to navigate to the registration view.
   *
   * @param event The event that triggered the action.
   */
  @FXML
  void handleRegisterAction(ActionEvent event) {

    loadView("/views/registration.fxml", event);
  }

  /**
   * Handles the action to navigate to the login view.
   *
   * @param event The event that triggered the action.
   */
  @FXML
  void handleLoginAction(ActionEvent event) {

    loadView("/views/login.fxml", event);
  }

  /**
   * Handles the Single Sign-On (SSO) action.
   * <p>
   * This method attempts to log in using credentials stored from a previous session.
   * If the credentials are present and valid, it logs the user in automatically
   * and navigates to the home view. If the credentials are missing, expired, or
   * if the login fails, it displays an appropriate alert to the user.
   *
   * @param event The event that triggered this action.
   */
  @FXML
  void handleSsoAction(ActionEvent event) {

    Optional<Credentials> credentials = credentialManagerService.loadCredentials();

    if (credentials.isPresent()) {
      boolean loginSuccessful = userService.login(credentials.get().getEmail(),
              credentials.get().getPassword());

      if (loginSuccessful) {
        loadView("/views/home.fxml", event);
      } else {
        showAlert(Alert.AlertType.ERROR, "SSO Login Failed", "Login was unsuccessful.\n" +
                "Please try again.");
      }
    } else {
      showAlert(Alert.AlertType.INFORMATION, "SSO", "SSO credentials are missing or have expired.\n" +
              "Login manually to activate new SSO session.");
    }
  }

  /**
   * Handles the logout action.
   * <p>
   * This method is triggered when the user clicks the logout button. It performs the logout
   * operation by clearing the current user session and then redirects the user to the home view.
   *
   * @param event The event that triggered this action, typically the logout button click.
   */
  @FXML
  void handleLogoutAction(ActionEvent event) {

    userSession.logout();
    loadView("/views/home.fxml", event);
  }

  /**
   * Updates the visibility of the registration button based on the user's login status.
   * The button is visible and managed if a user is currently logged in.
   */
  private void updateRegisterButtonVisibility() {

    User currentUser = userSession.getCurrentUser();
    registerButton.setVisible(currentUser != null);
    registerButton.setManaged(currentUser != null);
  }

  /**
   * Updates the visibility of the login button based on the user's login status.
   * The button is visible and managed if no user is currently logged in.
   */
  private void updateLoginButtonVisibility() {

    User currentUser = userSession.getCurrentUser();
    loginButton.setVisible(currentUser == null);
    loginButton.setManaged(currentUser == null);
  }

  private void updateSsoButtonVisibility() {

    User currentUser = userSession.getCurrentUser();
    ssoButton.setVisible(currentUser == null);
    ssoButton.setManaged(currentUser == null);
  }

  private void updateLogoutButtonVisibility() {

    User currentUser = userSession.getCurrentUser();
    logoutButton.setVisible(currentUser != null);
    logoutButton.setManaged(currentUser != null);
  }

  /**
   * Updates the welcome message based on the user's login status.
   * Displays a personalized welcome message if a user is logged in; hides the message otherwise.
   */
  private void updateUserWelcomeMessage() {

    User currentUser = userSession.getCurrentUser();
    if (currentUser != null) {
      welcomeUserText.setText("Hello, " + currentUser.getFirstName());
      welcomeMessageContainer.setVisible(true);
    } else {
      welcomeMessageContainer.setVisible(false);
    }
  }
}
