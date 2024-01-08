package bg.tuvarna.sit.wms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a DTO for the RequestDetails entity.
 * It is used for transferring the rental request details, when creating a rental request
 * from the presentation to the service layer.
 */
@Getter
@Setter
@NoArgsConstructor
public class RequestDetailsDTO {

  private WarehouseDTO warehouseDTO;
  private BigDecimal monthlyRent;
  private LocalDate startDate;
  private LocalDate endDate;
}
