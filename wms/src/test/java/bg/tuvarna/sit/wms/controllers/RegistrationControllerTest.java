package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.util.JpaUtil;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import static org.testfx.assertions.api.Assertions.assertThat;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class RegistrationControllerTest {

  @Start
  public void start(Stage stage) throws Exception {

    Parent root = FXMLLoader.load(getClass().getResource("/views/registration.fxml"));
    stage.setTitle("Login");
    stage.setScene(new Scene(root));
    stage.show();
  }

  @Test
  void handleRegister_ShouldShowOnlyFirstNameErrorLabelWhenFieldsAreEmpty(FxRobot robot) {

    robot.clickOn("#registerBtn");

    assertThat(robot.lookup("#firstNameErrorLabel").queryAs(Label.class))
            .hasText("First name can contain only letters and spaces.");
    assertTrue(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
  }

  @Test
  void handleRegister_ShouldShowFirstNameErrorLabelWhenFirstNameContainsIllegalCharacters(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("1Invalid_*Name");
    robot.clickOn("#registerBtn");

    assertTrue(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#firstNameErrorLabel").queryAs(Label.class))
            .hasText("First name can contain only letters and spaces.");
  }

  @Test
  void handleRegister_ShouldShowLastNameErrorLabelWhenLastNameContainsIllegalCharacters(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("1Invalid__");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#lastNameErrorLabel").queryAs(Label.class))
            .hasText("Last name can contain only letters and spaces.");
  }

  @Test
  void handleRegister_ShouldShowEmailErrorLabelWhenEmailIsInvalid(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("invalidEmail");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#emailErrorLabel").queryAs(Label.class)).hasText("The provided email is invalid.");
  }

  @Test
  void handleRegister_ShouldShowPasswordErrorLabelWhenPasswordDoesNotMeetRequirements(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#passwordErrorLabel").queryAs(Label.class))
            .hasText("Password should contain at least 8 symbols. At least one upper case letter and one special symbol.");
  }

  @Test
  void handleRegister_ShouldShowConfirmPasswordErrorLabelWhenPasswordsAreNotEqual(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("12345678*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("12345678");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class))
            .hasText("Passwords must match.");
  }

  @Test
  void handleRegister_ShouldShowPhoneErrorLabelWhenPhoneStartsWithPrefixAndHasInvalidNumberOfDigits(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("+3590878888888");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#phoneErrorLabel").queryAs(Label.class)).hasText("Enter a valid phone number.");
  }

  @Test
  void handleRegister_ShouldShowPhoneErrorLabelWhenPhoneStartsWithWrongPrefixAndHasValidNumberOfDigits(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("+040878888888");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#phoneErrorLabel").queryAs(Label.class)).hasText("Enter a valid phone number.");
  }

  @Test
  void handleRegister_ShouldShowPhoneErrorLabelWhenPhoneStartsWithoutPrefixAndHasInvalidNumberOfDigits(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("08788888881");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#phoneErrorLabel").queryAs(Label.class)).hasText("Enter a valid phone number.");
  }

  @Test
  void handleRegister_ShouldHaveValidUserWithPhoneNumberStartingWithAPrefix(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("+359878888888");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
  }

  @Test
  void handleRegister_ShouldHaveValidUserWithPhoneNumberStartingWithoutAPrefix(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("0878888888");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
  }

  @Test
  void handleRegister_ShouldShowRoleErrorLabelWhenRoleIsNotSelected(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("0878888888");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
    assertTrue(robot.lookup("#roleErrorLabel").queryAs(Label.class).isVisible());
    assertThat(robot.lookup("#roleErrorLabel").queryAs(Label.class)).hasText("Choosing a role is mandatory.");
  }

  @Test
  void handleRegister_ShouldRegisterUserSuccessfully(FxRobot robot) {

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("valid@email.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("0878888888");
    robot.clickOn("#roleBox");
    robot.clickOn("OWNER");
    robot.clickOn("#registerBtn");

    assertFalse(robot.lookup("#firstNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#lastNameErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#emailErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#passwordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#confirmPasswordErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#phoneErrorLabel").queryAs(Label.class).isVisible());
    assertFalse(robot.lookup("#roleErrorLabel").queryAs(Label.class).isVisible());

    Optional<User> userOptional = new UserDao(JpaUtil.getEntityManagerFactory()).findByEmail("valid@email.com");
    assertTrue(userOptional.isPresent());
  }
}