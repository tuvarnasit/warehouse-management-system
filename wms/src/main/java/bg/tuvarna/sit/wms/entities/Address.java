package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents the physical address of a warehouse.
 * <p>
 * It encapsulates critical information about a location, including the street, ZIP code,
 * and the city in which the warehouse is situated. It enables the association of warehouses
 * with specific addresses, allowing users to identify the precise location of each warehouse.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends BaseEntity {

  @Column(name = "street", nullable = false)
  private String street;

  @Column(name = "zip_code", nullable = false)
  private String zipCode;

  @ManyToOne
  @JoinColumn(name = "city_id", nullable = false, referencedColumnName = "id")
  private City city;
}
