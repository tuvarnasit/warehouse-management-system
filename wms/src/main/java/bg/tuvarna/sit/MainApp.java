package bg.tuvarna.sit;

import bg.tuvarna.sit.wms.util.JpaUtil;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManager;


public class MainApp extends Application {

  @Override
  public void start(Stage stage) throws IOException {

    Parent root = FXMLLoader.load(getClass().getResource("/views/home.fxml"));
    stage.setTitle("Home");
    stage.setScene(new Scene(root));
    stage.show();
  }

  public static void main(String[] args) {

    EntityManager entityManager = JpaUtil.getEntityManager();
    launch();
  }
}
