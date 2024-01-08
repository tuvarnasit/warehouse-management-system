package bg.tuvarna.sit.wms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representing an Agent entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class AgentDTO {

  private Long id;
  private String fullName;
  private String phone;
  private String email;

  @Override
  public String toString() {
    return fullName + ", " + email + ", " + phone;
  }
}
