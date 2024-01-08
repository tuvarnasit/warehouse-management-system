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
  Button loginButton;
  @FXML
  Button ssoButton;
  @FXML
  Text welcomeUserText;
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

    updateLoginButtonVisibility();
    updateSsoButtonVisibility();
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
        loadView("/views/application.fxml", event);
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
}
