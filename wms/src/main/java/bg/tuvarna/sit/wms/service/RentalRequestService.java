package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dao.WarehouseRentalRequestDAO;
import bg.tuvarna.sit.wms.dto.AgentDTO;
import bg.tuvarna.sit.wms.dto.RentalRequestDTO;
import bg.tuvarna.sit.wms.dto.RequestDetailsDTO;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.RequestDetails;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.entities.WarehouseRentalRequest;
import bg.tuvarna.sit.wms.enums.RequestStatus;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import bg.tuvarna.sit.wms.exceptions.RentalRequestDAOException;
import bg.tuvarna.sit.wms.exceptions.RequestCreationException;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing rental requests.
 * Handles the creation, accepting and declining of requests.
 */
public class RentalRequestService {

  private final WarehouseRentalRequestDAO warehouseRentalRequestDAO;
  private final WarehouseService warehouseService;
  private final UserDao userDao;
  private static final Logger LOGGER = LogManager.getLogger(RentalRequestService.class);

  public RentalRequestService(WarehouseRentalRequestDAO warehouseRentalRequestDAO, WarehouseService warehouseService, UserDao userDao) {
    this.warehouseRentalRequestDAO = warehouseRentalRequestDAO;
    this.warehouseService = warehouseService;
    this.userDao = userDao;
  }

  /**
   * Creates rental requests to a list of agents for a given warehouse.
   *
   * @param agentDTOS the agents who will receive the rental request
   * @param requestDetailsDTO a dto, containing rental details
   * @throws RequestCreationException if an error occurs during creation process
   * or the warehouse has an invalid status
   */
  public void createRentalRequests(List<AgentDTO> agentDTOS, RequestDetailsDTO requestDetailsDTO) throws RequestCreationException {

    if (!requestDetailsDTO.getWarehouseDTO().getStatus().equals(WarehouseStatus.AVAILABLE)) {
      throw new RequestCreationException("Error creating rental request: Only warehouses with available status allowed");
    }

    RequestDetails requestDetails = mapRequestDetailsDTOToEntity(requestDetailsDTO);
    for (AgentDTO agentDTO : agentDTOS) {
      WarehouseRentalRequest request = new WarehouseRentalRequest();
      request.setAgent(mapAgentDTOToEntity(agentDTO));
      request.setRequestDetails(requestDetails);
      request.setStatus(RequestStatus.PENDING);

      try {
        warehouseRentalRequestDAO.save(request);
      } catch (RentalRequestDAOException e) {
        String errorMessage = "Error creating warehouse rental request";
        LOGGER.error(errorMessage, e);
        throw new RequestCreationException(errorMessage, e);
      }
    }

    try {
      warehouseService.changeWarehouseStatus(requestDetailsDTO.getWarehouseDTO(), WarehouseStatus.PENDING_RENTAL);
    } catch (WarehouseServiceException e) {
      String errorMessage = "Error updating warehouse status during rental request creation";
      LOGGER.error(errorMessage, e);
      throw new RequestCreationException(errorMessage, e);
    }
  }

  /**
   * Handles the process of accepting a rental request, by invalidating requests
   * from other agents for the same warehouse, and then changing the status of the accepted request.
   *
   * @param rentalRequestDTO the request to be accepted
   * @throws RequestCreationException if an error occurs during any of the steps
   */
  public void acceptRentalRequest(RentalRequestDTO rentalRequestDTO) throws RequestCreationException {

    Warehouse warehouse = warehouseService.mapDTOToEntity(rentalRequestDTO.getWarehouseDTO());
    Agent agent = mapAgentDTOToEntity(rentalRequestDTO.getAgentDTO());
    List<WarehouseRentalRequest> otherAgentRequests = warehouseRentalRequestDAO.getAllByWarehouse(warehouse).stream()
        .filter(request -> !request.getAgent().getId().equals(agent.getId()))
        .toList();

    for (WarehouseRentalRequest rentalRequest : otherAgentRequests) {
      try {
        warehouseRentalRequestDAO.invalidateRequest(rentalRequest);
      } catch (RentalRequestDAOException e) {
        String errorMessage = "Error invalidating rental requests";
        LOGGER.error(errorMessage, e);
        throw new RequestCreationException(errorMessage, e);
      }
    }

    try {
      Optional<WarehouseRentalRequest> requestOptional = warehouseRentalRequestDAO.getById(rentalRequestDTO.getId());
      if (requestOptional.isEmpty()) {
        throw new RequestCreationException("Request was taken by another agent");
      }
      WarehouseRentalRequest request = requestOptional.get();
      warehouseRentalRequestDAO.changeStatus(request, RequestStatus.ACCEPTED);
    } catch (RentalRequestDAOException e) {
      String errorMessage = "Error while changing the rental request status to 'accepted'";
      LOGGER.error(errorMessage, e);
      throw new RequestCreationException(errorMessage, e);
    }
  }

  /**
   * Handles rental request declining, by setting its status to 'Declined' and invalidates the request.
   * Sets the warehouse status to 'Available' if all requests for the warehouse are invalidated.
   *
   * @param rentalRequestDTO the request to be declined
   * @throws RequestCreationException if an error occurs during the process
   */
  public void declineRentalRequest(RentalRequestDTO rentalRequestDTO) throws RequestCreationException {

    try {
      Optional<WarehouseRentalRequest> requestOptional = warehouseRentalRequestDAO.getById(rentalRequestDTO.getId());
      if (requestOptional.isEmpty()) {
        throw new RequestCreationException("Rental request chosen to be declined not found ");
      }
      WarehouseRentalRequest request = requestOptional.get();
      warehouseRentalRequestDAO.changeStatus(request, RequestStatus.DECLINED);
      warehouseRentalRequestDAO.invalidateRequest(request);

      Warehouse warehouse = warehouseService.mapDTOToEntity(rentalRequestDTO.getWarehouseDTO());
      List<WarehouseRentalRequest> requests = warehouseRentalRequestDAO.getAllByWarehouse(warehouse);
      if(requests.isEmpty()) {
        warehouseService.changeWarehouseStatus(rentalRequestDTO.getWarehouseDTO(), WarehouseStatus.AVAILABLE);
      }
    } catch (RentalRequestDAOException | WarehouseServiceException e) {
      String errorMessage = "Error while changing the rental request status to 'declined'";
      LOGGER.error(errorMessage, e);
      throw new RequestCreationException(errorMessage, e);
    }
  }

  /**
   * Handles rental request invalidation, by setting the isInvalid property of the request to true.
   * Sets the warehouse status to 'Available' if all requests for the warehouse are invalidated.
   *
   * @param rentalRequestDTO the request to be invalidated
   * @throws RequestCreationException if an error occurs during the process
   */
  public void invalidateRentalRequest(RentalRequestDTO rentalRequestDTO) throws RequestCreationException {

    Optional<WarehouseRentalRequest> requestOptional = warehouseRentalRequestDAO.getById(rentalRequestDTO.getId());
    if (requestOptional.isEmpty()) {
      throw new RequestCreationException("Rental request chosen to be declined not found ");
    }

    WarehouseRentalRequest request = requestOptional.get();
    try {
      warehouseRentalRequestDAO.invalidateRequest(request);
    } catch (RentalRequestDAOException e) {
      String errorMessage = "Error while invalidating rental request";
      LOGGER.error(errorMessage, e);
      throw new RequestCreationException(errorMessage, e);
    }
  }

  /**
   * Retrieves all requests for the given agent
   *
   * @param agent the specific agent for whom the requests are for.
   * @return a list of RentalRequestDTO objects representing incoming requests
   */
  public List<RentalRequestDTO> getIncomingRentalRequestsByAgent(Agent agent) {

    return warehouseRentalRequestDAO.getAllByStatusAndAgent(RequestStatus.PENDING, agent).stream()
        .map(this::mapWarehouseRentalRequestToDTO)
        .collect(Collectors.toList());
  }


  public List<RentalRequestDTO> getAcceptedRentalRequestsByAgent(Agent agent) {

    return warehouseRentalRequestDAO.getAllByStatusAndAgent(RequestStatus.ACCEPTED, agent).stream()
        .map(this::mapWarehouseRentalRequestToDTO)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves all agents and converts them to dtos
   *
   * @return List of AgentDTOs representing all of the agents
   */
  public List<AgentDTO> getAllAgentDTOs() {

    List<Agent> agents = userDao.getAllAgents();
    return agents.stream().map(this::mapAgentEntityToDTO).toList();
  }

  public RentalRequestDTO mapWarehouseRentalRequestToDTO(WarehouseRentalRequest requestEntity) {

    RentalRequestDTO dto = new RentalRequestDTO();
    WarehouseDTO warehouseDTO = warehouseService.mapEntityToDTO(requestEntity.getRequestDetails().getWarehouse());
    AgentDTO agentDTO = mapAgentEntityToDTO(requestEntity.getAgent());

    dto.setId(requestEntity.getId());
    dto.setWarehouseDTO(warehouseDTO);
    dto.setAgentDTO(agentDTO);
    dto.setStatus(requestEntity.getStatus());
    dto.setStartDate(requestEntity.getRequestDetails().getStartDate());
    dto.setEndDate(requestEntity.getRequestDetails().getEndDate());
    dto.setMonthlyRent(requestEntity.getRequestDetails().getPricePerMonth());

    return dto;
  }

  public Agent mapAgentDTOToEntity(AgentDTO agentDTO) {

    Agent agent = new Agent();
    agent.setId(agentDTO.getId());
    String[] names = agentDTO.getFullName().split(" ");
    agent.setFirstName(names[0]);
    agent.setLastName(names[1]);
    agent.setPhone(agentDTO.getPhone());
    agent.setEmail(agentDTO.getEmail());

    return agent;
  }

  public AgentDTO mapAgentEntityToDTO(Agent agent) {

    AgentDTO agentDTO = new AgentDTO();
    agentDTO.setId(agent.getId());
    agentDTO.setFullName(agent.getFirstName() + " " + agent.getLastName());
    agentDTO.setPhone(agent.getPhone());
    agentDTO.setEmail(agent.getEmail());

    return agentDTO;
  }

  public RequestDetails mapRequestDetailsDTOToEntity(RequestDetailsDTO dto) {

    RequestDetails requestDetails = new RequestDetails();
    Warehouse warehouse = warehouseService.mapDTOToEntity(dto.getWarehouseDTO());
    requestDetails.setWarehouse(warehouse);
    requestDetails.setStartDate(dto.getStartDate());
    requestDetails.setEndDate(dto.getEndDate());
    requestDetails.setPricePerMonth(dto.getMonthlyRent());

    return requestDetails;
  }

}
