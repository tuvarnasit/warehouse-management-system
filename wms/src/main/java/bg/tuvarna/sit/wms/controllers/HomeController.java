package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.session.UserSession;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
  Text welcomeUserText;
  @FXML
  StackPane welcomeMessageContainer;
  UserSession userSession = UserSession.getInstance();

  /**
   * Initializes the controller. This method is called after the FXML fields are populated.
   * It sets up the initial state of the UI components based on the user's login status.
   */
  @FXML
  protected void initialize() {
    updateRegisterButtonVisibility();
    updateLoginButtonVisibility();
    updateUserWelcomeMessage();
  }

  /**
   * Handles the action to navigate to the registration view.
   *
   * @param event The event that triggered the action.
   */
  @FXML
  protected void handleRegisterAction(ActionEvent event) {

    loadView("/views/registration.fxml", event);
  }

  /**
   * Handles the action to navigate to the login view.
   *
   * @param event The event that triggered the action.
   */
  @FXML
  protected void handleLoginAction(ActionEvent event) {

    loadView("/views/login.fxml", event);
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
