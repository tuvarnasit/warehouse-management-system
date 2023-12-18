package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * This class is the Data Access Object (DAO) for the Country entity.
 * Provides methods for preforming CRUD operations to the database on the Country entity.
 */
public class CountryDAO {

  /**
   * Persists a new country entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param country the country entity to persist
   * @throws CountryDAOException if an error occurs during the persistence of the entity
   */
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

  /**
   * Updates a country entity in the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param country the country entity to update
   * @throws CountryDAOException if an error occurs during the updating process of the entity
   */
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

  /**
   * Deletes a country entity from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param country the country entity to delete
   * @throws CountryDAOException if an error occurs during the deletion process of the entity
   */
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

  /**
   * Retrieves a country entity by id from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param id the id of the country entity
   * @return an Optional of the retrieved country, or an empty Optional if the country doesn't exist
   * @throws CountryDAOException if an error occurs during the retrieving process of the entity
   */
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

  /**
   * Retrieves a country entity by its name from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param name the name of the country entity
   * @return an Optional of the retrieved country, or an empty Optional if the country doesn't exist
   * @throws CountryDAOException if an error occurs during the retrieving process of the entity
   */
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

  /**
   * Retrieves all the country entities within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @return a list with all the countries
   * @throws CountryDAOException if an error occurs during the retrieving process of the entities
   */
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

  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param em the EntityManager
   * @param message the error message
   * @param e the exception to propagate
   * @return a new CountryDAOException with the given message and cause
   */
  private CountryDAOException handleExceptionAndRollback(EntityManager em, String message, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    return new CountryDAOException(message, e);
  }
}

