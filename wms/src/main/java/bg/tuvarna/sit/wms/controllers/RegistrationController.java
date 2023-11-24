package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

  private final UserService userService = new UserService();

  @FXML
  private TextField firstNameField;
  @FXML
  private TextField lastNameField;
  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private TextField phoneField;
  @FXML
  private ComboBox<String> roleBox;

  @FXML
  protected void handleRegistration() {

    UserRegistrationDto registrationDto = getUserRegistrationDto();
    userService.registerUser(registrationDto);
  }

  private UserRegistrationDto getUserRegistrationDto() {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName(firstNameField.getText());
    registrationDto.setLastName(lastNameField.getText());
    registrationDto.setEmail(emailField.getText());
    registrationDto.setPassword(passwordField.getText());
    registrationDto.setPhone(phoneField.getText());
    registrationDto.setRole(roleBox.getValue());

    return registrationDto;
  }
}
