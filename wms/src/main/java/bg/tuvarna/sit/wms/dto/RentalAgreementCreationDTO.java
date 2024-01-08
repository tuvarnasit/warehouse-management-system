package bg.tuvarna.sit.wms.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO representing the information required for creating a rental agreement.
 * Encapsulates details such as the associated rental request, warehouse, agent, tenant information,
 * rental period, and monthly rent.
 */
@Getter
@Setter
public class RentalAgreementCreationDTO {

  private RentalRequestDTO rentalRequestDTO;
  private WarehouseDTO warehouseDTO;
  private AgentDTO agentDTO;
  private String tenantFirstName;
  private String tenantLastName;
  private String companyName;
  private String companyId;
  private BigDecimal monthlyRent;
  private LocalDate startDate;
  private LocalDate endDate;
}
