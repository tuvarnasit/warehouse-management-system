package bg.tuvarna.sit.wms.session;

import lombok.Getter;

@Getter
public class Credentials {

  private final String email;
  private final String password;

  public Credentials(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
