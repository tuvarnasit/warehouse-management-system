package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents a city within the system.
 * <p>
 * The city entity is essential for categorizing and managing warehouses based on
 * their city of operation. It facilitates the geographical organization of warehouses
 * and allows users to search for warehouses based on their city location.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "cities")
@Getter
@Setter
public class City extends BaseEntity {

  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id")
  private Country country;
}
