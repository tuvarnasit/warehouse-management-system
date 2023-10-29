package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseNotification;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a specific notification intended for an agent.
 * <p>
 * Notifications are ways of conveying information or alerts to agents.
 * This entity inherits the common notification attributes from the
 * {@link BaseNotification} class. Each notification is associated
 * with a specific agent.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseNotification
 */
@Entity
@Table(name = "agent_notifications")
@Getter
@Setter
public class AgentNotification extends BaseNotification {

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false)
  private Agent agent;
}
