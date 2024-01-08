package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.service.CredentialManagerService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.session.UserSession;
import javafx.scene.control.Button;
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
  private UserService userService;
  private CredentialManagerService credentialManagerService;

  @BeforeEach
  public void setUp() {

    userSessionMock = mock(UserSession.class);
    controller = new HomeController(userService, credentialManagerService);

    controller.userSession = userSessionMock;
    controller.loginButton = new Button();
    controller.welcomeUserText = new Text();
    controller.ssoButton = new Button();
  }

  @Test
  void initialize_ShouldConfigureForLoggedInUser() {

    User mockUser = mock(User.class);
    when(mockUser.getFirstName()).thenReturn("John");
    when(userSessionMock.getCurrentUser()).thenReturn(mockUser);

    controller.initialize();

    assertFalse(controller.loginButton.isVisible());
    assertFalse(controller.loginButton.isManaged());
    assertFalse(controller.ssoButton.isVisible());
    assertFalse(controller.ssoButton.isManaged());
  }

  @Test
  void initialize_ShouldConfigureForNotLoggedInUser() {

    when(userSessionMock.getCurrentUser()).thenReturn(null);

    controller.initialize();

    assertTrue(controller.loginButton.isVisible());
    assertTrue(controller.loginButton.isManaged());
    assertTrue(controller.ssoButton.isVisible());
    assertTrue(controller.ssoButton.isManaged());
    assertEquals(controller.welcomeUserText.getText(), "");
  }
}
