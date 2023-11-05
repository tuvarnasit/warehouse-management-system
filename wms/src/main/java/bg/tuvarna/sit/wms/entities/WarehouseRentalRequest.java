package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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
  @JoinColumn(name = "warehouse_id", nullable = false, referencedColumnName = "id")
  private Warehouse warehouse;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false, referencedColumnName = "id")
  private Agent agent;
}
