package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

public class WarehouseDAO {

  public void save(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManager();
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

  public void update(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManager();
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

  public void delete(Warehouse warehouse) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManager();
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

  public Optional<Warehouse> getById(Long id) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManager();
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


  public List<Warehouse> getAll() throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManager();
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

  public List<Warehouse> getWarehousesByOwner(Owner owner) throws WarehouseDAOException {

    EntityManager em = JpaUtil.getEntityManager();
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

  public Optional<Warehouse> getWarehouseByNameAndOwner(String name, Owner owner) {

    EntityManager em = JpaUtil.getEntityManager();

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

  private WarehouseDAOException handleExceptionAndRollback(EntityManager em, String message, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    return new WarehouseDAOException(message, e);
  }
}
