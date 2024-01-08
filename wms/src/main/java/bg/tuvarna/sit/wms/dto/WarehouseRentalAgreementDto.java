package bg.tuvarna.sit.wms.dto;

import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WarehouseRentalAgreementDto {

  private String name;
  private String address;
  private Double size;
  private WarehouseStatus status;
  private String storageType;
  private ClimateCondition climateCondition;
  private Date startDate;
  private Date endDate;
  private BigDecimal pricePerMonth;
  private String agentFirstName;
  private String agentEmail;
  private Long agentId;
}

