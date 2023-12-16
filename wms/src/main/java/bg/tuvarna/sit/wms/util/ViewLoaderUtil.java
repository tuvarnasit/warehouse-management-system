package bg.tuvarna.sit.wms.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;

public class ViewLoaderUtil {

  private static final Logger LOGGER = LogManager.getLogger(ViewLoaderUtil.class);

  /**
   * Loads a view and sets it to the current stage.
   *
   * @param fxmlPath The path to the FXML file of the view.
   * @param event    The action event that triggered the view change.
   */
  public static void loadView(String fxmlPath, ActionEvent event) {

    try {
      Parent view = FXMLLoader.load(ViewLoaderUtil.class.getResource(fxmlPath));
      Scene scene = new Scene(view);
      Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
      window.setScene(scene);
      window.show();
    } catch (IOException e) {
      LOGGER.error("Failed to load the view: " + fxmlPath, e);
      showAlert("Error", "Failed to load the view.");
    } catch (Exception e) {
      LOGGER.error("An unexpected error occurred while loading the view: " + fxmlPath, e);
      showAlert("Error", "An unexpected error occurred.");
    }
  }

  private static void showAlert(String title, String content) {

    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
