package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import bg.tuvarna.sit.wms.enums.RequestStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents a rental request for a warehouse.
 * <p>
 * The owner of a warehouse can associate his warehouse with one or
 * multiple agents, indicating his intent to rent the property.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "warehouse_rental_requests")
@Getter
@Setter
public class WarehouseRentalRequest extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false, referencedColumnName = "id")
  private Agent agent;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "request_details_id", nullable = false, referencedColumnName = "id")
  private RequestDetails requestDetails;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private RequestStatus status;

  @Column(name = "is_invalid", nullable = false)
  private boolean isInvalid;

}
