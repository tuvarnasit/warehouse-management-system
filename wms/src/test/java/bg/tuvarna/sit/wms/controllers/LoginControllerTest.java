package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import static org.testfx.assertions.api.Assertions.assertThat;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

  @Start
  public void start(Stage stage) throws Exception {

    Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
    stage.setTitle("Login");
    stage.setScene(new Scene(root));
    stage.show();
  }

  @Test
  void handleLogin_ShouldLoginUserCorrectlyIfPresentInTheDatabase(FxRobot robot) {

    setupTestData();
    performLogin(robot, "test@wms.com", "securepassword1");
    assertThat(robot.lookup("#welcomeUserText").queryAs(Text.class)).hasText("Hello, First");

  }

  @Test
  void handleLogin_ShouldShowInvalidEmailLabelIfEmailIsInvalid(FxRobot robot) {

    performLogin(robot, "invalidEmail", "securepassword1");
    assertThat(robot.lookup("#emailErrorLabel").queryAs(Label.class)).hasText("The provided email is invalid.");
  }

  @Test
  void handleLogin_ShouldShowInvalidCredentialsLabelIfUserDoesNotExist(FxRobot robot) {

    performLogin(robot, "user@wms.com", "securepassword1");
    assertThat(robot.lookup("#authenticationErrorLabel").queryAs(Label.class)).hasText("Invalid email or password");
  }


  private void setupTestData() {

    User user = createUser();
    persistUser(user);
  }

  private User createUser() {

    User user = new User();
    user.setFirstName("First");
    user.setLastName("Last");
    user.setRole(Role.ADMIN);
    user.setEmail("test@wms.com");
    user.setPassword("1000:1bd1387f769fc3ddc99c366593114794:b2782feb84e3a13481fb2509270c4caaf3674a78dc4e6b9efb750eb98af0304c914480d8716705abe51321e786fee08d594c03536a146507751f8220b2247970");
    user.setPhone("phone");
    return user;
  }

  private void persistUser(User user) {

    EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();
    EntityTransaction entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();
    entityManager.persist(user);
    entityTransaction.commit();
  }

  private void performLogin(FxRobot robot, String email, String password) {

    robot.clickOn("#emailField");
    robot.write(email);
    robot.clickOn("#passwordField");
    robot.write(password);
    robot.clickOn("#loginButton");
  }
}
