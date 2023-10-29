package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseReview;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a review given by an owner to an agent.
 * <p>
 * This entity allows owners to rate and provide feedback about the performance
 * or service of an agent. This entity inherits the common attributes of a review
 * from the {@link BaseReview} class. The review associates both the owner
 * who provided it and the agent who received it.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseReview
 */
@Entity
@Table(name = "owner_to_agent_reviews")
@Getter
@Setter
public class OwnerToAgentReview extends BaseReview {

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false, referencedColumnName = "id")
  private Owner reviewer;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false, referencedColumnName = "id")
  private Agent agent;
}
