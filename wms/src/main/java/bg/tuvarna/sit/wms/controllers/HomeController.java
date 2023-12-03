package bg.tuvarna.sit.wms.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller class for the home screen of the application.
 * This class handles the user interaction with the home screen UI.
 *
 * @author Yavor Chamov
 * @version 1.0.0
 */
public class HomeController {

  private static final Logger LOGGER = LogManager.getLogger(HomeController.class);

  /**
   * Handles the action of the register button click.
   * This method attempts to load the registration view and set it to the current stage.
   *
   * @param event The action event generated when the register button is clicked.
   */
  @FXML
  protected void handleRegisterAction(ActionEvent event) {
    try {
      Parent registrationView = FXMLLoader.load(getClass().getResource("/views/registration.fxml"));
      Scene registrationScene = new Scene(registrationView);
      Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
      window.setScene(registrationScene);
      window.show();
    } catch (IOException e) {
      LOGGER.error("Failed to load the registration view.", e);
      showAlert("Error", "Failed to load the registration view.");
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred.", e);
      showAlert("Error", "An unexpected error occurred.");
    }
  }

  /**
   * Displays an alert dialog with a specified title and content.
   * The alert is of the ERROR type and will block application interaction until dismissed.
   *
   * @param title   The title of the alert dialog.
   * @param content The content message to display in the alert dialog.
   */
  private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
