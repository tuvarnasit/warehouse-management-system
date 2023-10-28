package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseNotification;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a specific notification intended for an owner.
 * <p>
 * Notifications are means to convey important messages or alerts to owners.
 * This entity inherits the common attributes of a notification from the
 * {@link BaseNotification} class. Each notification is linked to a
 * specific owner to whom it's directed.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseNotification
 */
@Entity
@Table(name = "owner_notifications")
@Getter
@Setter
public class OwnerNotification extends BaseNotification {

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private Owner owner;
}
