package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationController {
  @FXML
  private BorderPane pane;
  @FXML
  private Label welcomeLabel;
  @FXML
  private VBox sideMenu;

  private ToggleGroup toggleGroup = new ToggleGroup();
  private static final Logger LOGGER = LogManager.getLogger(ApplicationController.class);

  @FXML
  private void initialize() {

    addMenuButton("Warehouses", "/views/warehouseControlPanel.fxml");
    addMenuButton("Warehouses", "/views/warehouseControlPanel.fxml");
    addMenuButton("Warehouses", "/views/warehouseControlPanel.fxml");
    addMenuButton("Warehouses", "/views/warehouseControlPanel.fxml");
    addMenuButton("Warehouses", "/views/warehouseControlPanel.fxml");
    welcomeLabel.setText("Welcome to the Dashboard!");
  }

  private void addMenuButton(String text, String fxmlPath) {

    ToggleButton button = new ToggleButton(text);
    button.setToggleGroup(toggleGroup);
    button.getStyleClass().setAll("menu-button");
    button.setOnAction(e -> loadView(fxmlPath));
    sideMenu.getChildren().add(button);
  }

  private void loadView(String fxmlPath) {

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Node view = loader.load();
      WarehouseControlPanelController controller = loader.getController();
      pane.setCenter(view);
//      ExecutorService executorService = Executors.newSingleThreadExecutor();
//      executorService.submit(controller::initialize);
//      executorService.shutdown();
    } catch (IOException e) {
      LOGGER.error("Unable to load view: " + fxmlPath);
      DialogUtil.showErrorAlert("Error", "Error loading the view");
    } catch (Exception e) {
      LOGGER.error("Error ");
      DialogUtil.showErrorAlert("Error", "Error loading the view");
    }
  }
}
