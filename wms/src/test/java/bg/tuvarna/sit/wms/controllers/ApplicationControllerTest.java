package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.session.UserSession;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
class ApplicationControllerTest {

  private ApplicationController controller;
  private UserSession userSessionMock;

  @BeforeEach
  public void setUp() {

    userSessionMock = mock(UserSession.class);
    controller = new ApplicationController();
    controller.setUserSession(userSessionMock);
    controller.setSideMenu(new VBox());
    controller.setWelcomeSection(new VBox());
  }

  @Test
  void initialize_ShouldConfigureForLoggedInUser() {

    User mockUser = mock(User.class);
    when(mockUser.getFirstName()).thenReturn("John");
    when(userSessionMock.getCurrentUser()).thenReturn(mockUser);

    controller.initialize();

    Label loggedInUser = (Label) (findLabelById(controller.getWelcomeSection(), "loggedInUser").get());
    assertEquals(loggedInUser.getText(), "You are logged in as: John");
  }

  private Optional<Node> findLabelById(Pane container, String labelId) {

    for (Node node : new ArrayList<>(container.getChildren())) {
      if (node instanceof Label && labelId.equals(node.getId())) {
        return Optional.of(node);
      }
    }
    return Optional.empty();
  }
}
