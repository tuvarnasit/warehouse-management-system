package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CityDAOException;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

public class CityDAO {

  public void save(City city) throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(city);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error saving city entity", e);
    } finally {
      em.close();
    }
  }

  public void update(City city) throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.merge(city);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error updating city entity", e);
    } finally {
      em.close();
    }
  }

  public void delete(City city) throws CityDAOException {

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
      throw handleExceptionAndRollback(em, "Error deleting city entity", e);
    } finally {
      em.close();
    }
  }

  public Optional<City> getById(Long id) throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      City city = em.find(City.class, id);
      em.getTransaction().commit();

      return Optional.ofNullable(city);
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error retrieving city entity", e);
    } finally {
      em.close();
    }
  }

  public Optional<City> getByNameAndCountry(String name, Country country) throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();

    try {
      em.getTransaction().begin();
      City city = em.createQuery("SELECT c FROM City c WHERE c.name = :name AND c.country = :country", City.class)
          .setParameter("name", name).setParameter("country", country).getSingleResult();
      em.getTransaction().commit();

      return Optional.ofNullable(city);
    } catch (NoResultException e) {
      return Optional.empty();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving city entity by name", e);
    } finally {
      em.close();
    }
  }

  public List<City> getAll() throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      List<City> cities = em.createQuery("SELECT c FROM City c", City.class).getResultList();
      em.getTransaction().commit();

      return cities;
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving city entities", e);
    } finally {
      em.close();
    }
  }

  private CityDAOException handleExceptionAndRollback(EntityManager em, String message, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    return new CityDAOException(message, e);
  }
}
