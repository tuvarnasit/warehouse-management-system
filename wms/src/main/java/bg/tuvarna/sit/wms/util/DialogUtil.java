package bg.tuvarna.sit.wms.util;

import bg.tuvarna.sit.wms.contracts.DialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogUtil {
  public static void showDialog(String fxmlPath, String title, DialogController controller) {
    try {
      FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource(fxmlPath));

      loader.setController(controller);
      Parent root = loader.load();

      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.setScene(new Scene(root));
      dialogStage.setTitle(title);
      controller.setDialogStage(dialogStage);

      dialogStage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
      // TODO: handle the error
    }
  }
}
