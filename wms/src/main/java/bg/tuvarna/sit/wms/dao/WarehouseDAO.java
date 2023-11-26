package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WarehouseDAO {

  public void save(Warehouse warehouse) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(warehouse);
      em.getTransaction().commit();
    } catch (Exception e) {
      handleException(em, e);
    } finally {
      em.close();
    }
  }

  public void update(Warehouse warehouse) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.merge(warehouse);
      em.getTransaction().commit();
    } catch (Exception e) {
      handleException(em, e);
    } finally {
      em.close();
    }
  }

  public void delete(Warehouse warehouse) {

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
      handleException(em, e);
    } finally {
      em.close();
    }
  }

  public Optional<Warehouse> getById(Long id) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      Warehouse warehouse = em.find(Warehouse.class, id);
      em.getTransaction().commit();

      return Optional.ofNullable(warehouse);
    } catch (Exception e) {
      handleException(em, e);
      return Optional.empty();
    } finally {
      em.close();
    }
  }


  public List<Warehouse> getAll() {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      List<Warehouse> warehouses = em.createQuery("SELECT w FROM Warehouse w ", Warehouse.class).getResultList();
      em.getTransaction().commit();

      return warehouses;
    } catch (Exception e) {
      handleException(em, e);
      return Collections.emptyList();
    } finally {
      em.close();
    }
  }

  private void handleException(EntityManager em, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    e.printStackTrace();
    // TODO: log
  }
}
