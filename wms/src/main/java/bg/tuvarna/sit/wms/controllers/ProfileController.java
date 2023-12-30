package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseMenuController;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.session.UserSession;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ProfileController extends BaseMenuController {

  @FXML
  private GridPane profileSection;

  private User currentUser;

  @Override
  @FXML
  protected void initialize() {

    super.initialize();

    currentUser = UserSession.getInstance().getCurrentUser();
    updateProfile();
  }

  private void updateProfile() {

    if (currentUser != null) {

      addLabelToGrid("First name:", Optional.empty(), Optional.empty(), 0,0);
      addLabelToGrid(currentUser.getFirstName(), Optional.empty(), Optional.of("profile-label"),0,1);

      addLabelToGrid("Last name:", Optional.empty(), Optional.empty(), 1,0);
      addLabelToGrid(currentUser.getLastName(), Optional.empty(), Optional.of("profile-label"),1,1);

      addLabelToGrid("Phone number:", Optional.empty(), Optional.empty(), 2,0);
      addLabelToGrid(currentUser.getPhone(), Optional.empty(), Optional.of("profile-label"),2,1);

      addLabelToGrid("Email:", Optional.empty(), Optional.empty(), 3,0);
      addLabelToGrid(currentUser.getEmail(), Optional.empty(), Optional.of("profile-label"),3,1);

      addLabelToGrid("Role:", Optional.empty(), Optional.empty(), 4,0);
      addLabelToGrid(currentUser.getRole().name(), Optional.empty(), Optional.of("profile-label"),4,1);
    }
  }

  private void addLabelToGrid(String text, Optional<String> id, Optional<String> styleClasses, int rowIndex, int columnIndex) {

    Label label = createLabel(text, id, styleClasses);

    GridPane.setRowIndex(label, rowIndex);
    GridPane.setColumnIndex(label, columnIndex);

    addLabelToPane(label, profileSection);
  }
}
