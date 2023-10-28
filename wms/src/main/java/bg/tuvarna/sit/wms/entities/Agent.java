package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseUser;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an agent user within the system.
 * <p>
 * Agents may represent or manage certain properties or tasks on behalf of
 * owners. They have the ability to receive reviews from both owners and tenants.
 * Additionally, agents can receive various notifications.
 * This entity inherits the common user attributes from the {@link BaseUser} class.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseUser
 */
@Entity
@Table(name = "agents")
@Getter
@Setter
public class Agent extends BaseUser {

  @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
  private Set<OwnerToAgentReview> ownerToAgentReviews;

  @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
  private Set<TenantToAgentReview> tenantToAgentReviews;

  @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AgentNotification> notifications;
}
