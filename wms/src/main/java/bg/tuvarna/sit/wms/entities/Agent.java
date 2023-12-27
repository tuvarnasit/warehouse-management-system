package bg.tuvarna.sit.wms.entities;

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
 * This entity inherits the common user attributes from the {@link User} class.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see User
 */
@Entity
@Table(name = "agents")
@Getter
@Setter
public class Agent extends User {

  @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
  private Set<Review> receivedReviews;

  @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
  private Set<WarehouseRentalRequest> warehouseRentalRequests;

  @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
  private Set<RentalAgreement> rentalAgreements;
}
