package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing detailed information for a warehouse rental request.
 * Encapsulates key attributes: associated warehouse, monthly price, and start/end dates.
 */
@Entity
@Table(name="request_details")
@Getter
@Setter
public class RequestDetails extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "warehouse_id", nullable = false, referencedColumnName = "id")
  private Warehouse warehouse;

  @Column(name = "price_per_month", nullable = false)
  private BigDecimal pricePerMonth;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

}
