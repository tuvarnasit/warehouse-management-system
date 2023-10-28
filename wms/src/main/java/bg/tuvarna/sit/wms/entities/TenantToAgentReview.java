package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseReview;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a review given by a tenant to an agent.
 * <p>
 * This entity allows tenants to rate and provide feedback about the service
 * or experience they had with an agent. This entity inherits the common attributes
 * of a review from the {@link BaseReview} class. Each review connects both the tenant
 * who provided it and the agent who received it.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseReview
 */
@Entity
@Table(name = "tenant_to_agent_reviews")
@Getter
@Setter
public class TenantToAgentReview extends BaseReview {

  @ManyToOne
  @JoinColumn(name = "tenant_id", nullable = false, referencedColumnName = "id")
  private Tenant reviewer;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false, referencedColumnName = "id")
  private Agent agent;
}
