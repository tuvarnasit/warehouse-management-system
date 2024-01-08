package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.RentalAgreementDAO;
import bg.tuvarna.sit.wms.dto.RentalAgreementCreationDTO;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.RentalAgreement;
import bg.tuvarna.sit.wms.entities.Tenant;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import bg.tuvarna.sit.wms.exceptions.RentalAgreementCreationException;
import bg.tuvarna.sit.wms.exceptions.RentalAgreementDAOException;
import bg.tuvarna.sit.wms.exceptions.RequestCreationException;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.Date;

/**
 * Service class for handling the creation process of rental agreements.
 * Manages the persistence of rental agreement entities, invalidation of associated rental requests,
 * and updating the status of warehouses.
 */
public class RentalAgreementService {

  private final WarehouseService warehouseService;
  private final RentalRequestService rentalRequestService;
  private final RentalAgreementDAO rentalAgreementDAO;
  private static final Logger LOGGER = LogManager.getLogger(RentalRequestService.class);

  public RentalAgreementService(WarehouseService warehouseService, RentalRequestService rentalRequestService, RentalAgreementDAO rentalAgreementDAO) {
    this.warehouseService = warehouseService;
    this.rentalRequestService = rentalRequestService;
    this.rentalAgreementDAO = rentalAgreementDAO;
  }

  /**
   * Creates a new rental agreement based on the provided DTO.
   * Persists the rental agreement entity, invalidates the associated rental request,
   * and updates the status of the corresponding warehouse.
   *
   * @param rentalAgreementCreationDTO the DTO containing information for creating a rental agreement
   * @throws RentalAgreementCreationException if an error occurs during the creation process
   */
  public void createRentalAgreement(RentalAgreementCreationDTO rentalAgreementCreationDTO) throws RentalAgreementCreationException {

    RentalAgreement rentalAgreement = mapDTOToEntity(rentalAgreementCreationDTO);

    try {
      rentalAgreementDAO.save(rentalAgreement);
      rentalRequestService.invalidateRentalRequest(rentalAgreementCreationDTO.getRentalRequestDTO());
      warehouseService.changeWarehouseStatus(rentalAgreementCreationDTO.getWarehouseDTO(), WarehouseStatus.RENTED);
    } catch (RentalAgreementDAOException e) {
      String errorMessage = "Error during rental agreement creation process";
      LOGGER.error(errorMessage, e);
      throw new RentalAgreementCreationException(errorMessage, e);
    } catch (WarehouseServiceException e) {
      String errorMessage = "Error changing warehouse status during rental agreement creation process";
      LOGGER.error(errorMessage, e);
      throw new RentalAgreementCreationException(errorMessage, e);
    } catch (RequestCreationException e) {
      String errorMessage = "Error invalidating rental request during rental agreement creation process";
      LOGGER.error(errorMessage, e);
      throw new RentalAgreementCreationException(errorMessage, e);
    }

  }

  public RentalAgreement mapDTOToEntity(RentalAgreementCreationDTO dto) {
    RentalAgreement rentalAgreement = new RentalAgreement();

    Warehouse warehouse = warehouseService.mapDTOToEntity(dto.getWarehouseDTO());
    Agent agent = rentalRequestService.mapAgentDTOToEntity(dto.getAgentDTO());
    Tenant tenant = new Tenant();
    tenant.setFirstName(dto.getTenantFirstName());
    tenant.setLastName(dto.getTenantLastName());
    tenant.setCompanyName(dto.getCompanyName());
    tenant.setCompanyId(dto.getCompanyId());

    rentalAgreement.setWarehouse(warehouse);
    rentalAgreement.setAgent(agent);
    rentalAgreement.setTenant(tenant);
    rentalAgreement.setStartDate(Date.from(dto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    rentalAgreement.setEndDate(Date.from(dto.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    rentalAgreement.setPricePerMonth(dto.getMonthlyRent());

    return rentalAgreement;
  }
}
