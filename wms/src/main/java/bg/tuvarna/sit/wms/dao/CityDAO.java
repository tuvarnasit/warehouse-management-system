package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CityDAO {

  public void save(City city) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(city);
      em.getTransaction().commit();
    } catch (Exception e) {
      handleException(em, e);
    } finally {
      em.close();
    }
  }

  public void update(City city) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.merge(city);
      em.getTransaction().commit();
    } catch (Exception e) {
      handleException(em, e);
    } finally {
      em.close();
    }
  }

  public void delete(City city) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      if (em.contains(city)) {
        em.remove(city);
      } else {
        em.remove(em.merge(city));
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      handleException(em, e);
    } finally {
      em.close();
    }
  }

  public Optional<City> getById(Long id) {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      City city = em.find(City.class, id);
      em.getTransaction().commit();

      return Optional.ofNullable(city);
    } catch (Exception e) {
      handleException(em, e);
      return Optional.empty();
    } finally {
      em.close();
    }
  }

  public Optional<City> getByName(String name) {

    EntityManager em = JpaUtil.getEntityManager();

    try {
      em.getTransaction().begin();
      City city = em.createQuery("SELECT c FROM City c WHERE c.name = :name", City.class)
          .setParameter("name", name).getSingleResult();
      em.getTransaction().commit();

      return Optional.ofNullable(city);
    } catch (NoResultException e) {
      return Optional.empty();
    } catch (Exception e) {
      handleException(em, e);
      throw new RuntimeException("Error retrieving city by name", e);
    } finally {
      em.close();
    }
  }

  public List<City> getAll() {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      List<City> cities = em.createQuery("SELECT c FROM City c", City.class).getResultList();
      em.getTransaction().commit();

      return cities;
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
