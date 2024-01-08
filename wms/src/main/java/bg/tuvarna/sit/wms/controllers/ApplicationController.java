package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseMenuController;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.session.UserSession;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ApplicationController extends BaseMenuController {

  @Setter
  @Getter
  @FXML
  private VBox welcomeSection;

  @Setter
  private UserSession userSession = UserSession.getInstance();

  @Override
  @FXML

  protected void initialize() {

    super.initialize();

    addLabelToPane(createLabel("Welcome to the dashboard!"), welcomeSection);
    updateLoginMessageInfo();
  }

  /**
   * Updates the welcome message based on the user's login status.
   * Displays a personalized welcome message if a user is logged in; hides the message otherwise.
   */
  private void updateLoginMessageInfo() {

    User currentUser = userSession.getCurrentUser();
    if (currentUser != null) {
      addLabelToPane(createLabel("You are logged in as: " + currentUser.getFirstName(),
              Optional.of("loggedInUser")), welcomeSection);
    } else {
      removeLabelById(welcomeSection, "loggedInUser");
    }
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
