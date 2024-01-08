package bg.tuvarna.sit.wms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum which describes the different types of climate conditions in a warehouse.
 *
 * @author Viktor Denchev
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum ClimateCondition {

  TEMPERATURE_CONTROLLED("Temperature controlled"),
  HUMIDITY_CONTROLLED("Humidity controlled"),
  AMBIENT("Ambient temperature"),
  REFRIGERATED("Refrigerated"),
  ATMOSPHERE_CONTROLLED("Atmosphere controlled");

  private final String description;

  @Override
  public String toString() {
    return description;
  }
}