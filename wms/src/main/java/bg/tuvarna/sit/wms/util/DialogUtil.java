package bg.tuvarna.sit.wms.util;

import bg.tuvarna.sit.wms.contracts.DialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DialogUtil {
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

    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
