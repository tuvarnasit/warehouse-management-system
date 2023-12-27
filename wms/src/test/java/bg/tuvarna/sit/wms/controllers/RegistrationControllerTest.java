package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.util.JpaUtil;
import bg.tuvarna.sit.wms.util.ViewLoaderUtil;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import static org.testfx.assertions.api.Assertions.assertThat;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.service.query.NodeQuery;

@ExtendWith(ApplicationExtension.class)
class RegistrationControllerTest {

  @AfterEach
  void tearDown() {
    clearDatabase();
  }

  @Start
  public void start(Stage stage) {

    ViewLoaderUtil.loadView("/views/registration.fxml", stage);

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

  @Test
  void handleRegister_ShouldShowErrorAlertIfUserWithTheGivenEmailAlreadyExists(FxRobot robot) {

    persistUser(createUser());

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("test@wms.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("0878888888");
    robot.clickOn("#roleBox");
    robot.clickOn("OWNER");
    robot.clickOn("#registerBtn");

    String contentText = robot.lookup(".dialog-pane").queryAs(DialogPane.class).getContentText();
    assertEquals("A user with this email already exists.", contentText);
  }

  @Test
  void handleRegister_ShouldShowErrorAlertIfUserWithTheGivenPhoneNumberAlreadyExists(FxRobot robot) {

    persistUser(createUser());

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("test2@wms.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("0878888888");
    robot.clickOn("#roleBox");
    robot.clickOn("OWNER");
    robot.clickOn("#registerBtn");

    String contentText = robot.lookup(".dialog-pane").queryAs(DialogPane.class).getContentText();
    assertEquals("A user with this phone number already exists.", contentText);
  }

  @Test
  void handleRegister_ShouldShowErrorAlertIfUserWithTheGivenPhoneNumberAlreadyExistsWith359Prefix(FxRobot robot) {

    persistUser(createUser());

    robot.clickOn("#firstNameField");
    robot.write("Peter");
    robot.clickOn("#lastNameField");
    robot.write("Parker");
    robot.clickOn("#emailField");
    robot.write("test2@wms.com");
    robot.clickOn("#passwordField");
    robot.write("123456*A");
    robot.clickOn("#confirmPasswordField");
    robot.write("123456*A");
    robot.clickOn("#phoneField");
    robot.write("+359878888888");
    robot.clickOn("#roleBox");
    robot.clickOn("OWNER");
    robot.clickOn("#registerBtn");

    String contentText = robot.lookup(".dialog-pane").queryAs(DialogPane.class).getContentText();
    assertEquals("A user with this phone number already exists.", contentText);
  }

  private User createUser() {

    User user = new User();
    user.setFirstName("First");
    user.setLastName("Last");
    user.setRole(Role.ADMIN);
    user.setEmail("test@wms.com");
    user.setPassword("1000:1bd1387f769fc3ddc99c366593114794:b2782feb84e3a13481fb2509270c4caaf3674a78dc4e6b9efb750eb98af0304c914480d8716705abe51321e786fee08d594c03536a146507751f8220b2247970");
    user.setPhone("phone");
    user.setPhone("0878888888");

    return user;
  }

  private void persistUser(User user) {

    EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();
    EntityTransaction entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();
    entityManager.persist(user);
    entityTransaction.commit();
  }

  private Optional<Stage> getTopModalStage(FxRobot robot) {
    return Stage.getWindows().stream()
            .filter(window -> window instanceof Stage)
            .map(window -> (Stage) window)
            .filter(Window::isShowing)
            .findFirst();
  }

  private void clearDatabase() {
    EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    try {
      transaction.begin();

      // Assuming 'User' is your entity. Adapt the query to your needs.
      entityManager.createQuery("DELETE FROM User").executeUpdate();

      transaction.commit();
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new RuntimeException("Failed to clear database", e);
    } finally {
      entityManager.close();
    }
  }
}