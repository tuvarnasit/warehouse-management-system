package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.dto.WarehouseRentalAgreementDto;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import javax.persistence.TypedQuery;

/**
 * This class is the Data Access Object (DAO) for the Warehouse entity.
 * Provides methods for preforming CRUD operations to the database on the Warehouse entity.
 */
public class WarehouseDAO {

  private final EntityManagerFactory entityManagerFactory;

  public WarehouseDAO(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Persists a new warehouse entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouse the warehouse entity to persist
   * @throws WarehouseDAOException if an error occurs during the persistence of the entity
   */
  public void save(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      em.persist(warehouse);
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error saving warehouse entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Updates a warehouse entity in the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouse the warehouse entity to update
   * @throws WarehouseDAOException if an error occurs during the updating process of the entity
   */
  public void update(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      em.getTransaction().begin();
      em.merge(warehouse);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error updating warehouse entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Softly deletes a warehouse entity by setting the isDeleted flag of the warehouse to true
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouse the warehouse entity to delete
   * @throws WarehouseDAOException if an error occurs during the deletion process of the entity
   */
  public void softDelete(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      warehouse.setDeleted(true);
      em.merge(warehouse);
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error deleting warehouse entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves a warehouse entity from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param id the id of the warehouse entity
   * @return an Optional of the retrieved warehouse, or an empty Optional if the warehouse doesn't exist
   * @throws WarehouseDAOException if an error occurs during the retrieving process of the entity
   */
  public Optional<Warehouse> getById(Long id) throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      Warehouse warehouse = em.find(Warehouse.class, id);
      transaction.commit();

      if (warehouse != null && warehouse.isDeleted()) {
        return Optional.empty();
      }
      return Optional.ofNullable(warehouse);
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error retrieving warehouse", e);
    } finally {
      em.close();
    }
  }


  /**
   * Retrieves all warehouse entities from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @return a list with all warehouses
   * @throws WarehouseDAOException if an error occurs during the retrieving process of the entities
   */
  public List<Warehouse> getAll() throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      List<Warehouse> warehouses = em
              .createQuery("SELECT w FROM Warehouse w WHERE w.isDeleted = false", Warehouse.class)
              .getResultList();
      transaction.commit();

      return warehouses;
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error retrieving warehouse entities", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves all warehouse entities owned by an owner from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param owner the owner of the warehouses
   * @return a list of all warehouses owned by the owner
   * @throws WarehouseDAOException if an error occurs during the retrieving process of the entities
   */
  public List<Warehouse> getAllWarehousesByOwner(Owner owner) throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      String jpql = "SELECT w FROM Warehouse w WHERE w.owner = :owner AND w.isDeleted = false";
      List<Warehouse> warehouses = em.createQuery(jpql, Warehouse.class)
              .setParameter("owner", owner)
              .getResultList();
      transaction.commit();

      return warehouses;
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error retrieving warehouse entities", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves all available warehouse entities owned by an owner from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param owner the owner of the warehouses
   * @return a list of all available warehouses owned by the owner
   * @throws WarehouseDAOException if an error occurs during the retrieving process of the entities
   */
  public List<Warehouse> getAvailableWarehousesByOwner(Owner owner) throws WarehouseDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      String jpql = "SELECT w FROM Warehouse w WHERE w.owner = :owner AND w.status = :available AND w.isDeleted = false";
      List<Warehouse> warehouses = em.createQuery(jpql, Warehouse.class)
          .setParameter("owner", owner)
          .setParameter("available", WarehouseStatus.AVAILABLE)
          .getResultList();
      transaction.commit();

      return warehouses;
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction,"Error retrieving warehouse entities", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves a warehouse entity entity by owner and name from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param name  the name of the warehouse entity
   * @param owner the owner of the warehouse
   * @return an Optional of the retrieved warehouse, or an empty Optional if the warehouse doesn't exist
   */
  public Optional<Warehouse> getWarehouseByNameAndOwner(String name, Owner owner) {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      String jpql = "SELECT w FROM Warehouse w WHERE w.owner = :owner AND w.name = :name AND w.isDeleted = false";
      Warehouse warehouse = em.createQuery(jpql, Warehouse.class)
              .setParameter("name", name)
              .setParameter("owner", owner)
              .getSingleResult();
      transaction.commit();

      return Optional.ofNullable(warehouse);
    } catch (NoResultException e) {
      return Optional.empty();
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves a list of WarehouseRentalAgreementDto objects for a specific owner.
   * This method fetches warehouses along with their rental agreements, providing details
   * such as warehouse name, address, size, status, storage type, climate condition, and
   * relevant agent information.
   *
   * @param ownerId The ID of the owner for whom the warehouses with rental agreements are to be fetched.
   * @return A list of WarehouseRentalAgreementDto objects representing
   * the warehouses with rental agreements for the specified owner.
   */
  public List<WarehouseRentalAgreementDto> getWarehousesWithRentalAgreementsForOwner(Long ownerId) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      String jpql = "SELECT new bg.tuvarna.sit.wms.dto.WarehouseRentalAgreementDto(w.name, a.street, w.size, w.status, " +
              "w.storageType.typeName, w.climateCondition, ra.startDate, ra.endDate, ra.pricePerMonth, " +
              "agent.firstName, agent.email, agent.id) " +
              "FROM Warehouse w " +
              "JOIN w.owner owner " +
              "JOIN w.rentalAgreements ra " +
              "JOIN ra.agent agent " +
              "JOIN w.address a " +
              "WHERE owner.id = :ownerId AND w.isDeleted = false";

      TypedQuery<WarehouseRentalAgreementDto> query = entityManager.createQuery(jpql, WarehouseRentalAgreementDto.class);
      query.setParameter("ownerId", ownerId);
      return query.getResultList();
    } finally {
      entityManager.close();
    }
  }

  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param transaction the EntityTransaction instance
   * @param message     the error message
   * @param e           the exception to propagate
   * @return a new WarehouseDAOException with the given message and cause
   */
  private WarehouseDAOException handleExceptionAndRollback(EntityTransaction transaction, String message, Exception e) {

    if (transaction.isActive()) {
      transaction.rollback();
    }
    return new WarehouseDAOException(message, e);
  }
}
