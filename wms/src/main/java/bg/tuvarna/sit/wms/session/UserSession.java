package bg.tuvarna.sit.wms.session;

import bg.tuvarna.sit.wms.entities.User;
import lombok.Getter;

@Getter
public class UserSession {

  private User currentUser;

  private UserSession() {
  }

  private static class Holder {

    static final UserSession INSTANCE = new UserSession();
  }

  public static UserSession getInstance() {
    return Holder.INSTANCE;
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  public void logout() {
    currentUser = null;
  }
}
