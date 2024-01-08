package bg.tuvarna.sit.wms.dao;


import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.RequestDetails;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.entities.WarehouseRentalRequest;
import bg.tuvarna.sit.wms.enums.RequestStatus;
import bg.tuvarna.sit.wms.exceptions.RentalRequestDAOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

/**
 * A DAO class for the WarehouseRentalRequest entity.
 * Provides methods for performing CRUD operations to the database.
 */
public class WarehouseRentalRequestDAO {

  private final EntityManagerFactory entityManagerFactory;

  public WarehouseRentalRequestDAO(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Persists a new WarehouseRentalRequest entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouseRentalRequest the WarehouseRentalRequest entity to be saved
   * @throws RentalRequestDAOException if an error occurs while persisting the entity
   */
  public void save(WarehouseRentalRequest warehouseRentalRequest) throws RentalRequestDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      RequestDetails requestDetails = warehouseRentalRequest.getRequestDetails();
      if (requestDetails.getId() == null) {
        em.persist(requestDetails);
      }
      em.persist(warehouseRentalRequest);
      transaction.commit();

    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error saving WarehouseRentalRequest entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Changes the status of a WarehouseRentalRequest entity to that of the given request status object.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouseRentalRequest the WarehouseRentalRequest entity whose status is to be changed
   * @param requestStatus          the new status to set for the entity
   * @throws RentalRequestDAOException if an error occurs while changing the status
   */
  public void changeStatus(WarehouseRentalRequest warehouseRentalRequest, RequestStatus requestStatus) throws RentalRequestDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      Optional<WarehouseRentalRequest> refreshedRequestOptional = getById(warehouseRentalRequest.getId());
      if (refreshedRequestOptional.isEmpty()) {
        throw new RentalRequestDAOException("Error changing rental request status: Entity does not exist");
      }
      WarehouseRentalRequest refreshedRequest = refreshedRequestOptional.get();

      refreshedRequest.setStatus(requestStatus);
      em.merge(refreshedRequest);
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error changing rental request status to " + requestStatus.name(), e);
    } finally {
      em.close();
    }
  }

  /**
   * Sets the invalid flag of a rental request to true,
   * which will act as a mechanism to soft delete the given entity.
   *
   * @param warehouseRentalRequest the request to be invalidated
   * @throws RentalRequestDAOException if an error occurs during invalidation process
   */
  public void invalidateRequest(WarehouseRentalRequest warehouseRentalRequest) throws RentalRequestDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      warehouseRentalRequest.setInvalid(true);
      em.merge(warehouseRentalRequest);
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error invalidating WarehouseRentalRequest entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves a request entity from the database within a transaction.
   *
   * @param id the id of the WarehouseRentalRequest entity
   * @return an Optional of the retrieved request, or an empty Optional if the request doesn't exist/is invalidated
   */
  public Optional<WarehouseRentalRequest> getById(Long id) {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      WarehouseRentalRequest result = em.find(WarehouseRentalRequest.class, id);
      transaction.commit();

      if (result != null && result.isInvalid()) {
        return Optional.empty();
      }

      return Optional.ofNullable(result);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves all requests for a given warehouse entity within a transaction.
   *
   * @param warehouse the warehouse for the wanted rental requests
   * @return a list containing all rental requests of the warehouse entity
   */
  public List<WarehouseRentalRequest> getAllByWarehouse(Warehouse warehouse) {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    List<WarehouseRentalRequest> warehouseRentalRequests;
    try {
      transaction.begin();
      String jpql = "SELECT r FROM WarehouseRentalRequest r WHERE requestDetails.warehouse = :warehouse AND isInvalid = false";
      warehouseRentalRequests = em.createQuery(jpql, WarehouseRentalRequest.class)
          .setParameter("warehouse", warehouse)
          .getResultList();
      transaction.commit();
      return warehouseRentalRequests;
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves all rental requests, with a certain status, which are directed to the given agent within a transaction.
   *
   * @param requestStatus the status of the wanted rental requests
   * @param agent         the agent, to which all requests are addressed to
   * @return
   */
  public List<WarehouseRentalRequest> getAllByStatusAndAgent(RequestStatus requestStatus, Agent agent) {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    List<WarehouseRentalRequest> warehouseRentalRequests;
    try {
      transaction.begin();
      String jpql = "SELECT r FROM WarehouseRentalRequest r WHERE r.status = :status AND r.agent = :agent AND isInvalid = false";
      warehouseRentalRequests = em.createQuery(jpql, WarehouseRentalRequest.class)
          .setParameter("status", requestStatus)
          .setParameter("agent", agent)
          .getResultList();
      transaction.commit();
      return warehouseRentalRequests;
    } finally {
      em.close();
    }
  }


  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param transaction the EntityTransaction instance
   * @param message     the error message
   * @param e           the exception to propagate
   * @return a new RentalRequestDAOException with the given message and cause
   */
  private RentalRequestDAOException handleExceptionAndRollback(EntityTransaction transaction, String message, Exception e) {

    if (transaction.isActive()) {
      transaction.rollback();
    }
    return new RentalRequestDAOException(message, e);
  }
}
