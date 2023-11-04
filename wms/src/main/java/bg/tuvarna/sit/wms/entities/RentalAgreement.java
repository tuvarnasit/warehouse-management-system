package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a formal agreement between a warehouse owner, rental agent, and a tenant.
 * <p>
 * The agreement specifies the terms and conditions agreed upon by both parties for a particular warehouse.
 * This includes the start and end dates of the rental agreement and the monthly rental price.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "rental_agreements")
@Getter
@Setter
public class RentalAgreement extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "warehouse_id", nullable = false)
  private Warehouse warehouse;

  @ManyToOne
  @JoinColumn(name = "agent_id", nullable = false)
  private Agent agent;

  @ManyToOne
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;

  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Column(name = "end_date", nullable = false)
  @Temporal(TemporalType.DATE)
  private Date endDate;

  @Column(name = "price_per_month", nullable = false)
  private BigDecimal pricePerMonth;
}
