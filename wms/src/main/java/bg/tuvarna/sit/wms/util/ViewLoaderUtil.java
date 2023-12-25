package bg.tuvarna.sit.wms.util;

import bg.tuvarna.sit.wms.context.ApplicationContext;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewLoaderUtil {

  private static final Logger LOGGER = LogManager.getLogger(ViewLoaderUtil.class);

  /**
   * Loads a view and sets it to the current stage.
   *
   * @param fxmlPath The path to the FXML file of the view.
   * @param event    The action event that triggered the view change.
   */
  public static void loadView(String fxmlPath, ActionEvent event) {

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    loadView(fxmlPath, stage);
  }

  /**
   * Loads a JavaFX view and sets it to the provided stage.
   *
   * @param fxmlPath The path to the FXML file of the view.
   * @param stage    The stage where the view will be set.
   */
  public static void loadView(String fxmlPath, Stage stage) {

    try {
      Parent view = loadFXML(fxmlPath);
      setScene(stage, view);
    } catch (Exception e) {
      handleLoadViewException(e, fxmlPath);
    }
  }

  /**
   * Shows an alert dialog with the specified type, title, and content.
   *
   * @param type    The type of the alert.
   * @param title   The title of the alert.
   * @param content The content message to be displayed in the alert.
   */
  public static void showAlert(Alert.AlertType type, String title, String content) {

    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Loads an FXML file and returns its root node.
   *
   * @param fxmlPath The path to the FXML file.
   * @return The root node of the loaded FXML file.
   * @throws IOException If the file cannot be loaded.
   */
  private static Parent loadFXML(String fxmlPath) throws IOException {

    FXMLLoader loader = new FXMLLoader(ViewLoaderUtil.class.getResource(fxmlPath));
    loader.setControllerFactory(ApplicationContext.getCONTROLLER_FACTORY());
    return loader.load();
  }

  /**
   * Sets the provided view to the given stage.
   *
   * @param stage The stage to set the view on.
   * @param view  The root node of the JavaFX view to set on the stage.
   */
  private static void setScene(Stage stage, Parent view) {

    Scene scene = new Scene(view);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Handles exceptions that occur during the view loading process.
   *
   * @param e        The exception that was thrown.
   * @param fxmlPath The path of the FXML file that was being loaded.
   */
  private static void handleLoadViewException(Exception e, String fxmlPath) {

    LOGGER.error("Failed to load the view: " + fxmlPath, e);
    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the view.");
  }
}
