package bg.tuvarna.sit;

import bg.tuvarna.sit.wms.util.JpaUtil;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManager;


public class MainApp extends Application {

  @Override
  public void start(Stage stage) throws IOException {

    FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/scene.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
    stage.setTitle("Hello!");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {

    EntityManager entityManager = JpaUtil.getEntityManager();
    launch();
  }
}
