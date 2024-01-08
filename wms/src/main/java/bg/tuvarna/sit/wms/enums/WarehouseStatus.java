package bg.tuvarna.sit.wms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
@Getter
@RequiredArgsConstructor
public enum WarehouseStatus {

  AVAILABLE("Available"),
  PENDING_RENTAL("Pending"),
  RENTED("Rented"),
  ;

  private final String description;
}
