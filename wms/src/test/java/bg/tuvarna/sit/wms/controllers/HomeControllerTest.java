package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.session.UserSession;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
public class HomeControllerTest {

  private HomeController controller;
  private UserSession userSessionMock;

  @BeforeEach
  public void setUp() {

    userSessionMock = mock(UserSession.class);
    controller = new HomeController();
    controller.userSession = userSessionMock;
    controller.registerButton = new Button();
    controller.loginButton = new Button();
    controller.welcomeUserText = new Text();
    controller.welcomeMessageContainer = new StackPane();
  }

  @Test
  void initialize_ShouldConfigureForLoggedInUser() {

    User mockUser = mock(User.class);
    when(mockUser.getFirstName()).thenReturn("John");
    when(userSessionMock.getCurrentUser()).thenReturn(mockUser);

    controller.initialize();

    assertTrue(controller.registerButton.isVisible());
    assertTrue(controller.registerButton.isManaged());
    assertFalse(controller.loginButton.isVisible());
    assertFalse(controller.loginButton.isManaged());
    assertEquals(controller.welcomeUserText.getText(), "Hello, John");
    assertTrue(controller.welcomeMessageContainer.isVisible());
  }

  @Test
  void initialize_ShouldConfigureForNotLoggedInUser() {

    when(userSessionMock.getCurrentUser()).thenReturn(null);

    controller.initialize();

    assertFalse(controller.registerButton.isVisible());
    assertFalse(controller.registerButton.isManaged());
    assertTrue(controller.loginButton.isVisible());
    assertTrue(controller.loginButton.isManaged());
    assertEquals(controller.welcomeUserText.getText(), "");
    assertFalse(controller.welcomeMessageContainer.isVisible());
  }
}
