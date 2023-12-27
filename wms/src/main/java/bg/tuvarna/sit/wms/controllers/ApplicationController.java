package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.session.UserSession;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationController {

  @FXML
  private Label welcomeLabel;
  @FXML
  private VBox sideMenu;

  private final UserSession userSession = UserSession.getInstance();

  private final ToggleGroup toggleGroup = new ToggleGroup();
  private static final Logger LOGGER = LogManager.getLogger(ApplicationController.class);

  @FXML
  private void initialize() {

    addMenuButton("Warehouses", e -> loadView("/views/warehouseControlPanel.fxml", e));
    addMenuButton("Warehouses", e -> loadView("/views/warehouseControlPanel.fxml", e));
    addMenuButton("Warehouses", e -> loadView("/views/warehouseControlPanel.fxml", e));
    addMenuButton("Register", e -> loadView("/views/registration.fxml", e));
    addMenuButton("Logout", this::handleLogoutAction);
    welcomeLabel.setText("Welcome to the Dashboard!");
  }

  /**
   * Handles the logout action.
   * <p>
   * This method is triggered when the user clicks the logout button. It performs the logout
   * operation by clearing the current user session and then redirects the user to the home view.
   *
   * @param event The event that triggered this action, typically the logout button click.
   */
  @FXML
  void handleLogoutAction(ActionEvent event) {

    userSession.logout();
    loadView("/views/home.fxml", event);
  }

  private void addMenuButton(String text, EventHandler<ActionEvent> action) {

    ToggleButton button = new ToggleButton(text);
    button.setToggleGroup(toggleGroup);
    button.getStyleClass().setAll("menu-button");
    button.setOnAction(action);
    sideMenu.getChildren().add(button);
  }
}
