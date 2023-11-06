package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents the different types of storage a warehouse can hold.
 * <p>
 * Storage types define the nature and characteristics of storage spaces within warehouses,
 * providing essential information to both warehouse owners and tenants. Each storage type
 * can have a brief description, providing further information for tenants and agents to see.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Entity
@Table(name = "storage_types")
@Getter
@Setter
public class StorageType extends BaseEntity {

  @Column(name = "type_name", nullable = false)
  private String typeName;

  @Column(name = "description", length = 400)
  private String description;
}
