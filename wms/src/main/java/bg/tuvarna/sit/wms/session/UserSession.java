package bg.tuvarna.sit.wms.session;

import bg.tuvarna.sit.wms.entities.User;
import lombok.Getter;

/**
 * Singleton class for managing the user session in the application.
 * This class holds the currently logged-in user and provides methods
 * for setting the current user and logging out.
 */
@Getter
public class UserSession {

  private User currentUser;

  // Private constructor to prevent instantiation from outside the class
  private UserSession() {
  }

  /**
   * Holder class for ensuring thread-safe, lazy initialization of the singleton instance.
   */
  private static class Holder {
    static final UserSession INSTANCE = new UserSession();
  }

  /**
   * Gets the singleton instance of the UserSession.
   *
   * @return The singleton instance of UserSession.
   */
  public static UserSession getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * Sets the currently logged-in user for the session.
   *
   * @param user The user to set as the current user.
   */
  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  /**
   * Logs out the current user by setting the currentUser to null.
   */
  public void logout() {
    currentUser = null;
  }
}

