package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseNotification;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a specific notification intended for a tenant.
 * <p>
 * Notifications are crucial for conveying important messages or alerts to tenants,
 * whether it's about their lease, payment reminders, or other property-related information.
 * This entity inherits the common attributes of a notification from the
 * {@link BaseNotification} class. Each notification is linked to a
 * specific tenant to whom it's directed.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseNotification
 */
@Entity
@Table(name = "tenant_notifications")
@Getter
@Setter
public class TenantNotification extends BaseNotification {

  @ManyToOne
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;
}
