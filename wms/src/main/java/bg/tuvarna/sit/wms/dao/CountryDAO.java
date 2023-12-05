package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

public class CountryDAO {

  public void save(Country country) throws CountryDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(country);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error saving country entity", e);
    } finally {
      em.close();
    }
  }

  public void update(Country country) throws CountryDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.merge(country);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error updating country entity", e);
    } finally {
      em.close();
    }
  }

  public void delete(Country country) throws CountryDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      if (em.contains(country)) {
        em.remove(country);
      } else {
        em.remove(em.merge(country));
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error deleting country entity", e);
    } finally {
      em.close();
    }
  }

  public Optional<Country> getById(Long id) throws CountryDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      Country country = em.find(Country.class, id);
      em.getTransaction().commit();

      return Optional.ofNullable(country);
    } catch (Exception e) {
      throw handleExceptionAndRollback(em,"Error retrieving country entity", e);
    } finally {
      em.close();
    }
  }

  public Optional<Country> getByName(String name) throws CountryDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      Country country = em.createQuery("SELECT c FROM Country c WHERE c.name = :name", Country.class)
          .setParameter("name", name).getSingleResult();
      em.getTransaction().commit();

      return Optional.ofNullable(country);
    } catch (NoResultException e) {
      return Optional.empty();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving country entity by name", e);
    } finally {
      em.close();
    }
  }

  public List<Country> getAll() throws CountryDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      List<Country> countries = em.createQuery("SELECT c FROM Country c", Country.class).getResultList();
      em.getTransaction().commit();

      return countries;
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving country entities", e);
    } finally {
      em.close();
    }
  }

  private CountryDAOException handleExceptionAndRollback(EntityManager em, String message, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    return new CountryDAOException(message, e);
  }
}

