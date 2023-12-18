package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CityDAOException;
import bg.tuvarna.sit.wms.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * This class is the Data Access Object (DAO) for the City entity.
 * Provides methods for preforming CRUD operations to the database on the City entity.
 */
public class CityDAO {

  /**
   * Persists a new city entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param city the city entity to persist
   * @throws CityDAOException if an error occurs during the persistence of the entity
   */
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

  /**
   * Updates an existing city entity in the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param city the city entity to update
   * @throws CityDAOException if an error occurs during the updating process of the entity
   */
  public void update(City city) throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      em.merge(city);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error updating city entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Deletes a city entity from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param city the city entity to delete
   * @throws CityDAOException if an error occurs during the deletion process of the entity
   */
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

  /**
   * Retrieves a city entity by id from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param id the id of the city entity
   * @return an Optional of the retrieved city, or an empty Optional if the city doesnt exist
   * @throws CityDAOException if an error occurs during the retrieving process of the entity
   */
  public Optional<City> getById(Long id) throws CityDAOException {

    EntityManager em = JpaUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      City city = em.find(City.class, id);
      em.getTransaction().commit();

      return Optional.ofNullable(city);
    } catch (Exception e) {
      throw handleExceptionAndRollback(em, "Error retrieving city entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Retrieves a city entity by its name and country from the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param name the name of the city entity
   * @param country the country of the city entity
   * @return an Optional of the city entity or an empty Optional if such city doesn't exist
   * @throws CityDAOException if an error occurs during the retrieving process of the entity
   */
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

  /**
   * Retrieves all the city entities within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @return a list with all the cities
   * @throws CityDAOException if an error occurs during the retrieving process of the entities
   */
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

  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param em the EntityManager
   * @param message the error message
   * @param e the exception to propagate
   * @return a new CityDAOException with the given message and cause
   */
  private CityDAOException handleExceptionAndRollback(EntityManager em, String message, Exception e) {

    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
    return new CityDAOException(message, e);
  }
}
