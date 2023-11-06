package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a country within the system, serving as a fundamental
 * unit of geographic identification. It stores essential information about a country, allowing
 * for organized and structured data related to countries.
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "countries")
@Getter
@Setter
public class Country extends BaseEntity {

  @Column(name = "name", nullable = false)
  private String name;
}
