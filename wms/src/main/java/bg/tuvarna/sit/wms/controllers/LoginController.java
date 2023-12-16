package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.service.PasswordHashingService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.util.JpaUtil;
import static bg.tuvarna.sit.wms.util.ValidationUtils.showErrorLabel;
import static bg.tuvarna.sit.wms.util.ValidationUtils.validateField;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.loadView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private Label emailErrorLabel;
  @FXML
  private Label authenticationErrorLabel;

  private final UserService userService =
          new UserService(new UserDao(JpaUtil.getEntityManagerFactory()),new PasswordHashingService());

  @FXML
  protected void handleLogin(ActionEvent event) {

    String email = emailField.getText();
    String password = passwordField.getText();

    if (!validateInput()) {
      return;
    }

    boolean loginSuccessful = userService.login(email, password);

    if (loginSuccessful) {
      loadView("/views/home.fxml", event);
    } else {
      authenticationErrorLabel.setText("Invalid email or password");
      authenticationErrorLabel.setVisible(true);
    }
  }

  private boolean validateInput() {

    boolean isEmailValid = validateField(emailField, "\\S+@\\S+\\.\\S+");
    showErrorLabel(emailErrorLabel, "The provided email is invalid.", !isEmailValid);
    return isEmailValid;
  }
}
