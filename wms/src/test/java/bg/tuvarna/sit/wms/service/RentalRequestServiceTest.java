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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class RentalRequestServiceTest {

  @Mock
  private WarehouseRentalRequestDAO warehouseRentalRequestDAO;

  @Mock
  private WarehouseService warehouseService;

  @Mock
  private UserDao userDao;

  @InjectMocks
  private RentalRequestService rentalRequestService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createRentalRequest_shouldSaveRentalRequestEntity() {

    List<AgentDTO> agentDTOs = new ArrayList<>();
    RequestDetailsDTO requestDetailsDTO = createMockRequestDetailsDTO();
    agentDTOs.add(createMockAgentDTO());

    when(warehouseService.mapDTOToEntity(any())).thenReturn(createMockWarehouse());
    when(userDao.getAllAgents()).thenReturn(List.of(createMockAgent()));

    assertDoesNotThrow(() -> rentalRequestService.createRentalRequests(agentDTOs, requestDetailsDTO));
  }

  @Test
  void createRentalRequests_invalidWarehouseStatus_shouldThrowRequestCreationException() {

    List<AgentDTO> agentDTOs = new ArrayList<>();
    RequestDetailsDTO requestDetailsDTO = createMockRequestDetailsDTO();
    requestDetailsDTO.getWarehouseDTO().setStatus(WarehouseStatus.RENTED);
    agentDTOs.add(createMockAgentDTO());

    when(warehouseService.mapDTOToEntity(any())).thenReturn(createMockWarehouse());
    when(userDao.getAllAgents()).thenReturn(List.of(createMockAgent()));

    assertThrows(RequestCreationException.class, () -> rentalRequestService.createRentalRequests(agentDTOs, requestDetailsDTO));
  }

  @Test
  void acceptRentalRequest_shouldAcceptRequest() {

    RentalRequestDTO rentalRequestDTO = createMockRentalRequestDTO();

    when(warehouseRentalRequestDAO.getAllByWarehouse(any())).thenReturn(List.of(createMockWarehouseRentalRequest()));
    when(warehouseRentalRequestDAO.getById(any())).thenReturn(Optional.of(createMockWarehouseRentalRequest()));

    assertDoesNotThrow(() -> rentalRequestService.acceptRentalRequest(rentalRequestDTO));
  }

  @Test
  void acceptRentalRequest_entityNotFound_shouldThrowRequestCreationException() {

    RentalRequestDTO rentalRequestDTO = createMockRentalRequestDTO();

    when(warehouseRentalRequestDAO.getAllByWarehouse(any())).thenReturn(List.of());
    when(warehouseRentalRequestDAO.getById(any())).thenReturn(Optional.empty());

    assertThrows(RequestCreationException.class, () -> rentalRequestService.acceptRentalRequest(rentalRequestDTO));
  }

  @Test
  void declineRentalRequest_shouldDeclineRequest() {

    RentalRequestDTO rentalRequestDTO = createMockRentalRequestDTO();

    when(warehouseRentalRequestDAO.getById(any())).thenReturn(Optional.of(createMockWarehouseRentalRequest()));
    when(warehouseRentalRequestDAO.getAllByWarehouse(any())).thenReturn(List.of());
    when(warehouseService.mapDTOToEntity(any())).thenReturn(createMockWarehouse());

    assertDoesNotThrow(() -> rentalRequestService.declineRentalRequest(rentalRequestDTO));
  }

  @Test
  void getIncomingRentalRequestsByAgent_shouldRetrieveRequests() {

    Agent agent = createMockAgent();

    when(warehouseRentalRequestDAO.getAllByStatusAndAgent(any(), any())).thenReturn(List.of(createMockWarehouseRentalRequest()));

    List<RentalRequestDTO> result = rentalRequestService.getIncomingRentalRequestsByAgent(agent);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void getAllAgentDTOs_shouldRetrieveAgents() {

    List<Agent> agents = List.of(createMockAgent());

    when(userDao.getAllAgents()).thenReturn(agents);

    List<AgentDTO> result = rentalRequestService.getAllAgentDTOs();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  private RequestDetailsDTO createMockRequestDetailsDTO() {
    RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO();
    requestDetailsDTO.setWarehouseDTO(createMockWarehouseDTO());
    requestDetailsDTO.setStartDate(LocalDate.now());
    requestDetailsDTO.setEndDate(LocalDate.now().plusMonths(1));
    requestDetailsDTO.setMonthlyRent(BigDecimal.valueOf(1000));
    return requestDetailsDTO;
  }

  private WarehouseDTO createMockWarehouseDTO() {
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.setStatus(WarehouseStatus.AVAILABLE);
    return warehouseDTO;
  }

  private AgentDTO createMockAgentDTO() {
    AgentDTO agentDTO = new AgentDTO();
    agentDTO.setId(1L);
    agentDTO.setFullName("John Doe");
    agentDTO.setPhone("0987654321");
    agentDTO.setEmail("john.doe@example.com");
    return agentDTO;
  }

  private RentalRequestDTO createMockRentalRequestDTO() {
    RentalRequestDTO rentalRequestDTO = new RentalRequestDTO();
    rentalRequestDTO.setId(1L);
    rentalRequestDTO.setWarehouseDTO(createMockWarehouseDTO());
    rentalRequestDTO.setAgentDTO(createMockAgentDTO());
    rentalRequestDTO.setStatus(RequestStatus.PENDING);
    rentalRequestDTO.setStartDate(LocalDate.now());
    rentalRequestDTO.setEndDate(LocalDate.now().plusMonths(1));
    rentalRequestDTO.setMonthlyRent(BigDecimal.valueOf(1000));
    return rentalRequestDTO;
  }

  private Warehouse createMockWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setStatus(WarehouseStatus.AVAILABLE);
    return warehouse;
  }

  private Agent createMockAgent() {
    Agent agent = new Agent();
    agent.setId(1L);
    agent.setFirstName("John");
    agent.setLastName("Doe");
    agent.setPhone("0987654321");
    agent.setEmail("john.doe@example.com");
    return agent;
  }

  private WarehouseRentalRequest createMockWarehouseRentalRequest() {
    WarehouseRentalRequest rentalRequest = new WarehouseRentalRequest();
    rentalRequest.setId(1L);
    rentalRequest.setAgent(createMockAgent());
    rentalRequest.setRequestDetails(createMockRequestDetails());
    rentalRequest.setStatus(RequestStatus.PENDING);
    return rentalRequest;
  }

  private RequestDetails createMockRequestDetails() {
    RequestDetails requestDetails = new RequestDetails();
    requestDetails.setWarehouse(createMockWarehouse());
    requestDetails.setStartDate(LocalDate.now());
    requestDetails.setEndDate(LocalDate.now().plusMonths(1));
    requestDetails.setPricePerMonth(BigDecimal.valueOf(1000));
    return requestDetails;
  }
}
