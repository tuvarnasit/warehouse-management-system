package bg.tuvarna.sit.wms.dto;

import bg.tuvarna.sit.wms.enums.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO representing the RentalRequest entity.
 * Used in transferring the rental request data between the presentation and service layers
 */
@Getter
@Setter
@NoArgsConstructor
public class RentalRequestDTO {

  private Long id;
  private WarehouseDTO warehouseDTO;
  private AgentDTO agentDTO;
  private RequestStatus status;
  private BigDecimal monthlyRent;
  private LocalDate startDate;
  private LocalDate endDate;
}
