package bg.tuvarna.sit.wms.dto;

import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseDTO {
  private Long id;
  private String name;
  private String street;
  private String cityName;
  private String countryName;
  private String zipCode;
  private String storageType;
  private String storageTypeDescription;
  private Double size;
  private WarehouseStatus status;
  private ClimateCondition climateCondition;
  private Owner owner;
}
