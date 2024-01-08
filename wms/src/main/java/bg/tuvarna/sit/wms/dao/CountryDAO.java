package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * This class is the Data Access Object (DAO) for the Country entity.
 * Provides methods for preforming CRUD operations to the database on the Country entity.
 */
public class CountryDAO {

  private final EntityManagerFactory entityManagerFactory;

  public CountryDAO(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Persists a new country entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param country the country entity to persist
   * @throws CountryDAOException if an error occurs during the persistence of the entity
   */
  public void save(Country country) throws CountryDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      em.persist(country);
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction,"Error saving country entity", e);
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

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      em.merge(country);
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction,"Error updating country entity", e);
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

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      if (em.contains(country)) {
        em.remove(country);
      } else {
        em.remove(em.merge(country));
      }
      transaction.commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction,"Error deleting country entity", e);
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

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      Country country = em.find(Country.class, id);
      transaction.commit();

      return Optional.ofNullable(country);
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction,"Error retrieving country entity", e);
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

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      Country country = em.createQuery("SELECT c FROM Country c WHERE c.name = :name", Country.class)
          .setParameter("name", name).getSingleResult();
      transaction.commit();

      return Optional.ofNullable(country);
    } catch (NoResultException e) {
      return Optional.empty();
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error retrieving country entity by name", e);
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

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      List<Country> countries = em.createQuery("SELECT c FROM Country c", Country.class).getResultList();
      transaction.commit();

      return countries;
    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error retrieving country entities", e);
    } finally {
      em.close();
    }
  }

  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param transaction the EntityTransaction instance
   * @param message the error message
   * @param e the exception to propagate
   * @return a new CountryDAOException with the given message and cause
   */
  private CountryDAOException handleExceptionAndRollback(EntityTransaction transaction, String message, Exception e) {

    if (transaction.isActive()) {
      transaction.rollback();
    }
    return new CountryDAOException(message, e);
  }
}

