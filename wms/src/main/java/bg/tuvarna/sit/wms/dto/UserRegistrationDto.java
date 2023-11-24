package bg.tuvarna.sit.wms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
