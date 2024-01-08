package bg.tuvarna.sit.wms.dao;


import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.RequestDetails;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.entities.WarehouseRentalRequest;
import bg.tuvarna.sit.wms.enums.RequestStatus;
import bg.tuvarna.sit.wms.exceptions.RentalRequestDAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WarehouseRentalRequestDAOTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityTransaction transaction;

  @Mock
  private TypedQuery<WarehouseRentalRequest> query;

  @InjectMocks
  private WarehouseRentalRequestDAO warehouseRentalRequestDAO;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
    entityManager = mock(EntityManager.class);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.isOpen()).thenReturn(true);
    when(transaction.isActive()).thenReturn(true);
  }

  @Test
  void save_shouldSaveRentalRequest() {
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();

    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(true);

    assertDoesNotThrow(() -> {
      warehouseRentalRequestDAO.save(warehouseRentalRequest);
    });

    verify(transaction, times(1)).begin();
    verify(entityManager, times(1)).persist(warehouseRentalRequest);
    verify(transaction, times(1)).commit();
  }

  @Test
  void save_shouldRollbackAndThrowException() {
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();
    EntityTransaction transaction = mock(EntityTransaction.class);

    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(true);
    doThrow(new RuntimeException("Simulated exception")).when(entityManager).persist(any());

    assertThrows(RentalRequestDAOException.class, () -> {
      warehouseRentalRequestDAO.save(warehouseRentalRequest);
    });

    verify(transaction, times(1)).begin();
    verify(entityManager, never()).persist(warehouseRentalRequest.getRequestDetails());
    verify(transaction, times(1)).rollback();
  }

  @Test
  void changeStatus_shouldChangeRentalRequestStatus() {
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();

    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(true);
    when(entityManager.find(eq(WarehouseRentalRequest.class), any())).thenReturn(warehouseRentalRequest);

    assertDoesNotThrow(() -> {
      warehouseRentalRequestDAO.changeStatus(warehouseRentalRequest, RequestStatus.ACCEPTED);
    });

    verify(transaction, times(2)).begin();
    verify(entityManager, times(1)).find(eq(WarehouseRentalRequest.class), any());
    verify(transaction, times(2)).commit();
  }

  @Test
  void changeStatus_entityNotFound_shouldThrowException() throws RentalRequestDAOException {
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();

    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(true);
    when(entityManager.find(eq(WarehouseRentalRequest.class), any())).thenReturn(null);

    assertThrows(RentalRequestDAOException.class, () -> {
      warehouseRentalRequestDAO.changeStatus(warehouseRentalRequest, RequestStatus.ACCEPTED);
    });

    verify(transaction, times(2)).begin();
    verify(entityManager, times(1)).find(eq(WarehouseRentalRequest.class), any());
    verify(transaction, times(1)).rollback();
  }

  @Test
  void invalidateRequest_shouldInvalidateRentalRequest() {
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();

    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(true);

    assertDoesNotThrow(() -> {
      warehouseRentalRequestDAO.invalidateRequest(warehouseRentalRequest);
    });

    verify(transaction, times(1)).begin();
    verify(entityManager, times(1)).merge(warehouseRentalRequest);
    verify(transaction, times(1)).commit();
  }

  @Test
  void invalidateRequest_shouldThrowException() {
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();

    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(true);
    doThrow(new RuntimeException("Simulated exception")).when(entityManager).merge(any());

    assertThrows(RentalRequestDAOException.class, () -> {
      warehouseRentalRequestDAO.invalidateRequest(warehouseRentalRequest);
    });

    verify(transaction, times(1)).begin();
    verify(entityManager, times(1)).merge(warehouseRentalRequest);
    verify(transaction, times(1)).rollback();
  }

  @Test
  void getById_shouldRetrieveRequest() {
    long requestId = 1L;
    WarehouseRentalRequest warehouseRentalRequest = createMockWarehouseRentalRequest();
    when(entityManager.find(eq(WarehouseRentalRequest.class), any())).thenReturn(warehouseRentalRequest);

    Optional<WarehouseRentalRequest> result = warehouseRentalRequestDAO.getById(requestId);

    assertTrue(result.isPresent());
    assertEquals(warehouseRentalRequest, result.get());

    verify(entityManager, times(1)).find(eq(WarehouseRentalRequest.class), any());
  }

  @Test
  void getAllByWarehouse_shouldRetrieveRequestsForAWarehouse() {
    Warehouse warehouse = createMockWarehouse();
    List<WarehouseRentalRequest> mockRequests = createMockWarehouseRentalRequests(5);

    when(entityManager.createQuery(anyString(), eq(WarehouseRentalRequest.class))).thenReturn(query);
    when(query.setParameter(anyString(), any())).thenReturn(query);
    when(query.getResultList()).thenReturn(mockRequests);

    List<WarehouseRentalRequest> result = warehouseRentalRequestDAO.getAllByWarehouse(warehouse);

    assertEquals(mockRequests, result);

    verify(entityManager, times(1)).createQuery(anyString(), eq(WarehouseRentalRequest.class));
    verify(query, times(1)).setParameter(anyString(), any());
    verify(query, times(1)).getResultList();
  }

  @Test
  void getAllByStatusAndAgent_shouldRetrieveRequests() {
    RequestStatus requestStatus = RequestStatus.PENDING;
    Agent agent = createMockAgent();
    List<WarehouseRentalRequest> mockRequests = createMockWarehouseRentalRequests(5);

    when(entityManager.createQuery(anyString(), eq(WarehouseRentalRequest.class))).thenReturn(query);
    when(query.setParameter(anyString(), any())).thenReturn(query);
    when(query.getResultList()).thenReturn(mockRequests);

    List<WarehouseRentalRequest> result = warehouseRentalRequestDAO.getAllByStatusAndAgent(requestStatus, agent);

    assertEquals(mockRequests, result);

    verify(entityManager, times(1)).createQuery(anyString(), eq(WarehouseRentalRequest.class));
    verify(query, times(2)).setParameter(anyString(), any());
    verify(query, times(1)).getResultList();
  }

  private WarehouseRentalRequest createMockWarehouseRentalRequest() {
    WarehouseRentalRequest warehouseRentalRequest = new WarehouseRentalRequest();
    warehouseRentalRequest.setId(1L);
    warehouseRentalRequest.setRequestDetails(createMockRequestDetails());
    warehouseRentalRequest.setAgent(createMockAgent());
    warehouseRentalRequest.setStatus(RequestStatus.PENDING);
    warehouseRentalRequest.setInvalid(false);
    return warehouseRentalRequest;
  }

  private RequestDetails createMockRequestDetails() {
    RequestDetails requestDetails = new RequestDetails();
    requestDetails.setId(1L);
    return requestDetails;
  }

  private Warehouse createMockWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId(1L);
    return warehouse;
  }

  private Agent createMockAgent() {
    Agent agent = new Agent();
    agent.setId(1L);
    return agent;
  }

  private List<WarehouseRentalRequest> createMockWarehouseRentalRequests(int count) {
    return List.of(new WarehouseRentalRequest(), new WarehouseRentalRequest(),
        new WarehouseRentalRequest(), new WarehouseRentalRequest(),
        new WarehouseRentalRequest());
  }
}
