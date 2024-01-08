package bg.tuvarna.sit;

import bg.tuvarna.sit.wms.context.ApplicationContext;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.session.KeyUtil;
import bg.tuvarna.sit.wms.util.ViewLoaderUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javafx.application.Application;
import javafx.stage.Stage;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main application class for the JavaFX application.
 * This class is responsible for loading and displaying the primary stage and its contents.
 */
public class MainApp extends Application {

  private static final Logger LOGGER = LogManager.getLogger(MainApp.class);

  /**
   * Starts the JavaFX application by setting the primary stage.
   *
   * @param stage The primary stage for this application, onto which the application scene can be set.
   * @throws IOException If the FXML file for the home view cannot be loaded.
   */
  @Override
  public void start(Stage stage) throws IOException {

    stage.setTitle("Home");
    ViewLoaderUtil.loadView("/views/home.fxml", stage);
  }

  /**
   * The main entry point for all JavaFX applications.
   * The launch method is called to start the JavaFX application.
   *
   * @param args The command line arguments passed to the application. Not used in this application.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Initializes the application before the JavaFX application thread is started.
   * This method is called after the JavaFX system is initialized and before the
   * application start method is called.
   *
   * @throws Exception if an error occurs during initialization.
   */
  @Override
  public void init() throws Exception {
    super.init();
    initializeApplication();
  }

  /**
   * Performs application-wide initialization tasks.
   * Specifically, it initializes administrators in the system.
   */
  private void initializeApplication() {

    try {
      ApplicationContext.getUSER_SERVICE().initializeUsers();
      String keyFilename = "encryption.key";
      Path keyPath = Paths.get(keyFilename);
      if (!Files.exists(keyPath)) {
        SecretKey key = ApplicationContext.getENCRYPTION_SERVICE().generateKey();
        KeyUtil.saveSecretKey(key, keyFilename);
      }
    } catch (RegistrationException | InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
      LOGGER.error("Error during application initialization: ", e);
    }
  }
}

