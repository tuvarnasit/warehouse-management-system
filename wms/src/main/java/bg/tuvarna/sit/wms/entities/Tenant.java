package bg.tuvarna.sit.wms.entities;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a tenant user within the system.
 * <p>
 * Tenants are users who lease or rent properties. They have the ability
 * to receive notifications pertinent to them. This entity inherits the
 * common user attributes from the {@link User} class. Each tenant
 * can have multiple notifications associated with their account.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see User
 */
@Entity
@Table(name = "tenants")
@Getter
@Setter
public class Tenant extends User {

  @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
  private Set<RentalAgreement> rentalAgreements;

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<Review> sentReviews;
}
