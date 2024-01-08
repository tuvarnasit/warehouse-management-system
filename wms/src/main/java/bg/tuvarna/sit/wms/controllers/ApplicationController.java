package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.session.UserSession;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ApplicationController {

  @Setter
  @FXML
  private VBox sideMenu;

  @Setter
  @Getter
  @FXML
  private VBox welcomeSection;

  @Setter
  private UserSession userSession = UserSession.getInstance();

  private final ToggleGroup toggleGroup = new ToggleGroup();

  @FXML
  void initialize() {

    addMenuButton("Warehouses", Optional.empty(), Optional.empty(), e -> loadView("/views/warehouseControlPanel.fxml", e));
    addMenuButton("Rent a warehouse", Optional.empty(), Optional.empty(), e -> loadView("/views/warehouseRental.fxml", e));
    addMenuButton("Requests", Optional.empty(), Optional.empty(), e -> loadView("/views/incomingRequests.fxml", e));
    addMenuButton("Warehouses", Optional.empty(), Optional.empty(), e -> loadView("/views/warehouseControlPanel.fxml", e));
    addMenuButton("Register", Optional.of("registerButton"), Optional.empty(), e -> loadView("/views/registration.fxml", e));
    addMenuButton("Logout", Optional.of("logoutButton"), Optional.of("button-logout"), this::handleLogoutAction);
    addMenuLabel("Welcome to the dashboard!", Optional.empty());
    updateLoginMessageInfo();
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

  private void addMenuButton(String text, Optional<String> id, Optional<String> styleClasses, EventHandler<ActionEvent> action) {

    ToggleButton button = new ToggleButton(text);
    id.ifPresent(button::setId);
    button.setToggleGroup(toggleGroup);

    // Set default style class and add any additional styles if provided
    button.getStyleClass().add("menu-button");
    styleClasses.ifPresent(classes -> button.getStyleClass().addAll(classes.split(" ")));

    button.setOnAction(action);
    sideMenu.getChildren().add(button);
  }

  /**
   * Updates the welcome message based on the user's login status.
   * Displays a personalized welcome message if a user is logged in; hides the message otherwise.
   */
  private void updateLoginMessageInfo() {

    User currentUser = userSession.getCurrentUser();
    if (currentUser != null) {
      addMenuLabel("You are logged in as: " + currentUser.getFirstName(),
              Optional.of("loggedInUser"));
    } else {
      removeLabelById(welcomeSection, "loggedInUser");
    }
  }

  private void addMenuLabel(String text, Optional<String> id) {

    Label label = new Label(text);
    id.ifPresent(label::setId);
    welcomeSection.getChildren().add(label);
  }

  private void removeLabelById(Pane container, String labelId) {

    for (Node node : new ArrayList<>(container.getChildren())) {
      if (node instanceof Label && labelId.equals(node.getId())) {
        container.getChildren().remove(node);
        break;
      }
    }
  }
}
