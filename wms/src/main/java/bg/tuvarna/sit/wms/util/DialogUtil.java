package bg.tuvarna.sit.wms.util;

import bg.tuvarna.sit.wms.contracts.DialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * This class provides utility methods for displaying JavaFX dialogs and alerts.
 */
public class DialogUtil {

  /**
   * Displays a new dialog, loaded from a FXML file.
   * The functionality of the dialog is provided by the controller.
   *
   * @param fxmlPath the path to the FXML file, containing the dialog view
   * @param title the title of the dialog window
   * @param controller the controller for the dialog
   * @throws IOException if an error occurs while loading the FXML file
   */
  public static void showDialog(String fxmlPath, String title, DialogController controller) throws IOException {

      FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource(fxmlPath));

      loader.setController(controller);
      Parent root = loader.load();

      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.setScene(new Scene(root));
      dialogStage.setTitle(title);
      dialogStage.setResizable(false);
      controller.setDialogStage(dialogStage);

      dialogStage.showAndWait();
  }

  public static boolean showConfirmationDialog(String title, String description) {

    Alert confirmDelete = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);
    confirmDelete.setTitle(title);
    confirmDelete.setHeaderText(description);

    Optional<ButtonType> resultOptional = confirmDelete.showAndWait();

    return resultOptional.isPresent() && resultOptional.get() == ButtonType.YES;
  }

}
