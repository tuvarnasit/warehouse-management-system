package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseUser;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an owner user within the system.
 * <p>
 * Owners may possess or manage properties and have the ability to receive
 * notifications. This entity inherits the common user attributes from
 * the {@link BaseUser} class. Each owner can have multiple notifications
 * associated with them.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseUser
 */
@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
public class Owner extends BaseUser {

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OwnerNotification> notifications;
}
