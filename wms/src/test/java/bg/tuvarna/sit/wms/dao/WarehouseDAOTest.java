package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WarehouseDAOTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityTransaction transaction;

  @Mock
  private TypedQuery<Warehouse> query;

  @InjectMocks
  private WarehouseDAO warehouseDAO;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.isOpen()).thenReturn(true);
    when(transaction.isActive()).thenReturn(true);
  }

  @Test
  void save_shouldSaveWarehouse() {

    Warehouse warehouse = new Warehouse();

    assertDoesNotThrow(() -> warehouseDAO.save(warehouse));

    verify(transaction).begin();
    verify(entityManager).persist(warehouse);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void save_shouldRollbackAndThrowWarehouseDAOException() {

    Warehouse warehouse = new Warehouse();
    doThrow(new RuntimeException("Error during persistence")).when(entityManager).persist(warehouse);

    assertThrows(WarehouseDAOException.class, () -> warehouseDAO.save(warehouse));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void update_shouldUpdateWarehouse() {

    Warehouse warehouse = new Warehouse();

    assertDoesNotThrow(() -> warehouseDAO.update(warehouse));

    verify(transaction).begin();
    verify(entityManager).merge(warehouse);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void update_shouldRollbackAndThrowWarehouseDAOException() {

    Warehouse warehouse = new Warehouse();
    doThrow(new RuntimeException("Test Exception")).when(entityManager).merge(warehouse);

    assertThrows(WarehouseDAOException.class, () -> warehouseDAO.update(warehouse));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void softDelete() {

      Warehouse warehouse = new Warehouse();

      assertDoesNotThrow(() -> warehouseDAO.softDelete(warehouse));
      assertTrue(warehouse.isDeleted(), "Warehouse should be marked as deleted.");

      verify(transaction).begin();
      verify(entityManager).merge(warehouse);
      verify(transaction).commit();
      verify(entityManager).close();
  }

  @Test
  void softDelete_shouldRollbackAndThrowWarehouseDAOException() {

    Warehouse warehouse = new Warehouse();
    doThrow(new RuntimeException("Exception during deletion")).when(entityManager).merge(warehouse);

    assertThrows(WarehouseDAOException.class, () -> warehouseDAO.softDelete(warehouse));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void getById_shouldReturnWarehouseOptional() throws WarehouseDAOException {

    Long warehouseId = 1L;
    Warehouse warehouse = new Warehouse();
    warehouse.setId(warehouseId);
    when(entityManager.find(Warehouse.class, warehouseId)).thenReturn(warehouse);

    Optional<Warehouse> result = warehouseDAO.getById(warehouseId);

    assertTrue(result.isPresent());
    assertEquals(warehouse, result.get());

    verify(transaction).begin();
    verify(entityManager).find(Warehouse.class, warehouseId);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void getById_deletedWarehouse_shouldReturnEmptyOptional() throws WarehouseDAOException {

    Long deletedWarehouseId = 2L;
    Warehouse deletedWarehouse = new Warehouse();
    deletedWarehouse.setId(deletedWarehouseId);
    deletedWarehouse.setDeleted(true);
    when(entityManager.find(Warehouse.class, deletedWarehouseId)).thenReturn(deletedWarehouse);

    Optional<Warehouse> result = warehouseDAO.getById(deletedWarehouseId);

    assertFalse(result.isPresent(), "Should return an empty optional for a deleted warehouse.");

    verify(transaction).begin();
    verify(entityManager).find(Warehouse.class, deletedWarehouseId);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void getById_nonexistentWarehouse_shouldReturnEmptyOptional() throws WarehouseDAOException {

    Long nonExistentWarehouseId = 3L;
    when(entityManager.find(Warehouse.class, nonExistentWarehouseId)).thenReturn(null);

    Optional<Warehouse> result = warehouseDAO.getById(nonExistentWarehouseId);

    assertFalse(result.isPresent(), "Should return an empty optional for a non existent warehouse.");

    verify(transaction).begin();
    verify(entityManager).find(Warehouse.class, nonExistentWarehouseId);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void getById_shouldRollbackAndThrowWarehouseDAOException() {

    Long warehouseId = 4L;
    when(entityManager.find(Warehouse.class, warehouseId)).thenThrow(new RuntimeException("Exception during retrieval"));

    assertThrows(WarehouseDAOException.class, () -> warehouseDAO.getById(warehouseId));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void getWarehousesByOwner_shouldReturnListWithWarehouses() throws WarehouseDAOException {

    Owner owner = new Owner();
    Warehouse warehouse1 = new Warehouse();
    warehouse1.setOwner(owner);
    Warehouse warehouse2 = new Warehouse();
    warehouse2.setOwner(owner);
    List<Warehouse> expectedWarehouses = Arrays.asList(warehouse1, warehouse2);
    when(entityManager.createQuery(anyString(), eq(Warehouse.class))).thenReturn(query);
    when(query.setParameter("owner", owner)).thenReturn(query);
    when(query.getResultList()).thenReturn(expectedWarehouses);

    List<Warehouse> result = warehouseDAO.getAllWarehousesByOwner(owner);

    assertEquals(expectedWarehouses.size(), result.size());
    assertTrue(result.containsAll(expectedWarehouses));

    verify(transaction).begin();
    verify(query).setParameter("owner", owner);
    verify(query).getResultList();
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void getWarehousesByOwner_shouldRollbackAndThrowWarehouseDAOException() {

    Owner owner = new Owner();
    when(entityManager.createQuery(anyString(), eq(Warehouse.class))).thenReturn(query);
    when(query.setParameter("owner", owner)).thenReturn(query);
    when(query.getResultList()).thenThrow(new RuntimeException("Error during retrieval"));

    assertThrows(WarehouseDAOException.class, () -> warehouseDAO.getAllWarehousesByOwner(owner));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void getWarehouseByNameAndOwner_shouldReturnWarehouseOptional() {

    Owner owner = new Owner();
    String warehouseName = "Warehouse1";
    Warehouse expectedWarehouse = new Warehouse();
    expectedWarehouse.setName(warehouseName);
    expectedWarehouse.setOwner(owner);
    when(entityManager.createQuery(anyString(), eq(Warehouse.class))).thenReturn(query);
    when(query.setParameter("name", warehouseName)).thenReturn(query);
    when(query.setParameter("owner", owner)).thenReturn(query);
    when(query.getSingleResult()).thenReturn(expectedWarehouse);

    Optional<Warehouse> result = warehouseDAO.getWarehouseByNameAndOwner(warehouseName, owner);

    assertTrue(result.isPresent());
    assertEquals(expectedWarehouse, result.get());

    verify(transaction).begin();
    verify(query).setParameter("name", warehouseName);
    verify(query).setParameter("owner", owner);
    verify(query).getSingleResult();
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void getWarehouseByNameAndOwner_nonExistentWarehouse_shouldReturnEmptyOptional() {

    Owner owner = new Owner();
    String warehouseName = "Non Existent Warehouse";
    when(entityManager.createQuery(anyString(), eq(Warehouse.class))).thenReturn(query);
    when(query.setParameter("name", warehouseName)).thenReturn(query);
    when(query.setParameter("owner", owner)).thenReturn(query);
    when(query.getSingleResult()).thenThrow(new NoResultException());

    Optional<Warehouse> result = warehouseDAO.getWarehouseByNameAndOwner(warehouseName, owner);

    assertFalse(result.isPresent());

    verify(transaction).begin();
    verify(query).setParameter("name", warehouseName);
    verify(query).setParameter("owner", owner);
    verify(transaction, never()).commit();
    verify(entityManager).close();
  }

}