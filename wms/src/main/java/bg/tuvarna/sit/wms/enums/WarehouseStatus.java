package bg.tuvarna.sit.wms.enums;

/**
 * This enum represents the rental status of a warehouse.
 * <p>
 * The status indicates whether a warehouse is available for rental,
 * is in the process of finding tenants or is currently rented. This
 * insures that a warehouse cannot be concurrently at the same time by
 * multiple tenants.
 * </p>
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
public enum WarehouseStatus {

  AVAILABLE,
  PENDING_RENTAL,
  RENTED,
}
