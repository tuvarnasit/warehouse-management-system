package bg.tuvarna.sit.wms.controllers.base;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.session.UserSession;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class BaseMenuController {

  @Setter
  @FXML
  protected VBox sideMenu;

  private final ToggleGroup toggleGroup = new ToggleGroup();

  protected BaseMenuController() {}

  @FXML
  protected void initialize() {

    buildMenu();
  }

  protected void buildMenu() {

    addToggleButtonToPane(
            createToggleButton("Warehouses", e -> loadView("/views/warehouseControlPanel.fxml", e)),
            sideMenu);

    addToggleButtonToPane(
            createToggleButton("My Profile", e -> loadView("/views/profile.fxml", e)),
            sideMenu);

    Predicate<User> isAdminPredicate = user -> Role.ADMIN.equals(user.getRole());
    addToggleButtonToPane(createToggleButton("Register", e -> loadView("/views/registration.fxml", e),
            Optional.of("registerButton")), sideMenu, isAdminPredicate);

    Predicate<User> isAgentPredicate = user -> Role.AGENT.equals(user.getRole());
    addToggleButtonToPane(createToggleButton("My Reviews", e -> loadView("/views/reviews.fxml", e)),
            sideMenu, isAgentPredicate);

    addToggleButtonToPane(createToggleButton("Logout", this::handleLogoutAction, Optional.of("logoutButton"),
            Optional.of("button-logout")), sideMenu);
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

    UserSession.getInstance().logout();
    loadView("/views/home.fxml", event);
  }

  protected Label createLabel(String text) {

    return new Label(text);
  }

  protected Label createLabel(String text, Optional<String> id) {

    Label label = createLabel(text);
    id.ifPresent(label::setId);

    return label;
  }

  protected Label createLabel(String text, Optional<String> id, Optional<String> styleClasses) {

    Label label = createLabel(text, id);
    styleClasses.ifPresent(classes -> label.getStyleClass().addAll(classes.split(" ")));

    return label;
  }

  protected void addLabelToPane(Label label, Pane pane) {

    pane.getChildren().add(label);
  }

  private ToggleButton createToggleButton(String text, EventHandler<ActionEvent> action) {

    ToggleButton button = new ToggleButton(text);
    button.setToggleGroup(toggleGroup);
    button.getStyleClass().add("menu-button");
    button.setOnAction(action);

    return button;
  }

  private ToggleButton createToggleButton(String text, EventHandler<ActionEvent> action, Optional<String> id) {

    ToggleButton button = createToggleButton(text, action);
    id.ifPresent(button::setId);

    return button;
  }

  private ToggleButton createToggleButton(String text, EventHandler<ActionEvent> action,
                                          Optional<String> id, Optional<String> styleClasses) {

    ToggleButton button = createToggleButton(text, action, id);
    styleClasses.ifPresent(classes -> button.getStyleClass().addAll(classes.split(" ")));

    return button;
  }

  private void addToggleButtonToPane(ToggleButton toggleButton, Pane pane) {

    pane.getChildren().add(toggleButton);
  }

  private void addToggleButtonToPane(ToggleButton toggleButton, Pane pane, Predicate<User> visibilityPredicate) {
    if (visibilityPredicate.test(UserSession.getInstance().getCurrentUser())) {
      pane.getChildren().add(toggleButton);
    }
  }
}
