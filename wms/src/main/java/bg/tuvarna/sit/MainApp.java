package bg.tuvarna.sit;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


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

    EntityManagerFactory factory = Persistence.createEntityManagerFactory("wms");
    EntityManager entityManager = factory.createEntityManager();
    launch();
  }
}
