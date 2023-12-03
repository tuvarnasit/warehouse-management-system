package bg.tuvarna.sit.wms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for user registration.
 * <p>
 * This class is responsible for transferring user registration data between
 * the presentation layer and the service layer.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDto {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String phone;
  private String role;
}
