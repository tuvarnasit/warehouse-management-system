package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Set;

/**
 * Describes a warehouse available for rental.
 * <p>
 * Warehouses are physical storage spaces with various attributes
 * and configurations. Every warehouse has only one owner. The status
 * attribute describes the warehouse's current rental status.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse extends BaseEntity {

  @Column(name="name", nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false, referencedColumnName = "id")
  private Owner owner;

  @Column(name = "size", nullable = false)
  private Double size;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id", nullable = false, referencedColumnName = "id")
  private Address address;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private WarehouseStatus status;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "storage_type_id", nullable = false, referencedColumnName = "id")
  private StorageType storageType;

  @Enumerated(EnumType.STRING)
  @Column(name = "climate_condition", nullable = false)
  private ClimateCondition climateCondition;

  @Column(name="is_deleted", nullable = false)
  private boolean isDeleted = false;

  @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
  private Set<RentalAgreement> rentalAgreements;
}




