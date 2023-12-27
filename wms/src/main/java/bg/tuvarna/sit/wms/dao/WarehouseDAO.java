package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * This class is the Data Access Object (DAO) for the Warehouse entity.
 * Provides methods for preforming CRUD operations to the database on the Warehouse entity.
 */
public class WarehouseDAO {

  /**
   * Persists a new warehouse entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouse the warehouse entity to persist
   * @throws WarehouseDAOException if an error occurs during the persistence of the entity
   */
  public void save(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(warehouse);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error saving warehouse entity", e);
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

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      em.merge(warehouse);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error updating warehouse entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Deletes a warehouse entity from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param warehouse the warehouse entity to delete
   * @throws WarehouseDAOException if an error occurs during the deletion process of the entity
   */
  public void delete(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      if (em.contains(warehouse)) {
        em.remove(warehouse);
      } else {
        em.remove(em.merge(warehouse));
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error deleting warehouse entity", e);
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

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      Warehouse warehouse = em.find(Warehouse.class, id);
      em.getTransaction().commit();

      return Optional.ofNullable(warehouse);
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving warehouse", e);
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

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      List<Warehouse> warehouses = em.createQuery("SELECT w FROM Warehouse w ", Warehouse.class).getResultList();
      em.getTransaction().commit();

      return warehouses;
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving warehouse entities", e);
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
  public List<Warehouse> getWarehousesByOwner(Owner owner) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
    try {
      em.getTransaction().begin();
      List<Warehouse> warehouses = em.createQuery("SELECT w FROM Warehouse w WHERE w.owner = :owner", Warehouse.class)
          .setParameter("owner", owner)
          .getResultList();
      em.getTransaction().commit();

      return warehouses;
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error retrieving warehouse entities", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves a warehouse entity entity by owner and name from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param name the name of the warehouse entity
   * @param owner the owner of the warehouse
   * @return an Optional of the retrieved warehouse, or an empty Optional if the warehouse doesn't exist
   */
  public Optional<Warehouse> getWarehouseByNameAndOwner(String name, Owner owner) {

    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

    try {
      em.getTransaction().begin();
      Warehouse warehouse = em.createQuery("SELECT w FROM Warehouse w WHERE w.owner = :owner AND w.name = :name", Warehouse.class)
          .setParameter("name", name)
          .setParameter("owner", owner)
          .getSingleResult();
      em.getTransaction().commit();

      return Optional.ofNullable(warehouse);
    } catch (NoResultException e) {
      return Optional.empty();
    } finally {
      em.close();
    }
  }

  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param em the EntityManager
   * @param message the error message
   * @param e the exception to propagate
   * @return a new WarehouseDAOException with the given message and cause
   */
  private WarehouseDAOException handleExceptionAndRollback(EntityManager em, String message, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    return new WarehouseDAOException(message, e);
  }
}
