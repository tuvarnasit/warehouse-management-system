package bg.tuvarna.sit.wms.entities.base;

import bg.tuvarna.sit.wms.enums.Role;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the foundational attributes of a user within the system.
 * Serves as a base class for specific user entities to inherit common
 * user attributes.
 * <p>
 * This class is marked as a mapped superclass, meaning it won't be persisted
 * as an entity by itself. However, its attributes will be inherited by its
 * subclasses which represent concrete user entities.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseUser extends BaseEntity {

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "phone", nullable = false, unique = true)
  private String phone;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;
}
