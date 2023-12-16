package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data Access Object (DAO) for user operations.
 * Provides an abstraction layer for database operations related to user entities.
 */
public class UserDao {

  private static final Logger LOGGER = LogManager.getLogger(UserDao.class);

  private final EntityManagerFactory entityManagerFactory;

  public UserDao(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Persists a user entity to the database.
   * Handles transaction management and ensures the user is saved within a transaction context.
   *
   * @param user The user entity to persist.
   * @throws UserPersistenceException If there is a persistence error during the saving process.
   */
  public void saveUser(User user) throws UserPersistenceException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction entityTransaction = entityManager.getTransaction();

    try {
      entityTransaction.begin();
      entityManager.persist(user);
      entityTransaction.commit();
    } catch (PersistenceException e) {
      handleTransactionRollback(entityTransaction);
      throw new UserPersistenceException("Persistence error saving user", e);
    } catch (Exception e) {
      handleTransactionRollback(entityTransaction);
      throw new UserPersistenceException("Unexpected error saving user", e);
    } finally {
      if (entityManager.isOpen()) {
        entityManager.close();
      }
    }
  }

  public Optional<User> findByEmail(String email) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    Map<String, Object> properties = entityManager.getProperties();
    try {
      String jpql = "SELECT u FROM User u WHERE u.email = :email";
      TypedQuery<User> query = entityManager.createQuery(jpql, User.class)
              .setParameter("email", email);
      User user = query.getSingleResult();

      return Optional.of(user);
    } catch (NoResultException e) {
      return Optional.empty();
    } finally {
      entityManager.close();
    }
  }

  public Optional<User> findByPhone(String phone) {

    EntityManager entityManager = getEntityManager();

    try {
      String jpql = "SELECT u FROM User u WHERE u.phone = :phone";
      TypedQuery<User> query = entityManager.createQuery(jpql, User.class)
              .setParameter("phone", phone);
      User user = query.getSingleResult();

      return Optional.of(user);
    } catch (NoResultException e) {
      return Optional.empty();
    } finally {
      entityManager.close();
    }
  }

  /**
   * Retrieves the hashed password of a user by their ID.
   *
   * @param userId The ID of the user.
   * @return An Optional containing the hashed password if found, or an empty Optional otherwise.
   */
  public Optional<String> getUserPasswordById(Long userId) {

    EntityManager entityManager = getEntityManager();

    try {
      String jpql = "SELECT u.password FROM User u WHERE u.id = :userId";
      String password = entityManager.createQuery(jpql, String.class)
              .setParameter("userId", userId)
              .getSingleResult();
      return Optional.ofNullable(password);
    } catch (NoResultException e) {
      return Optional.empty();
    } finally {
      entityManager.close();
    }
  }

  /**
   * Handles the rollback of a transaction in case of an error.
   * If the transaction is active, it attempts to roll back the transaction and logs any rollback failures.
   *
   * @param entityTransaction The entity transaction which needs to be rolled back.
   */
  private void handleTransactionRollback(EntityTransaction entityTransaction) {

    try {
      if (entityTransaction.isActive()) {
        entityTransaction.rollback();
      }
    } catch (Exception e) {
      LOGGER.error("Transaction rollback failed", e);
    }
  }

  private EntityManager getEntityManager() {

    return entityManagerFactory.createEntityManager();
  }

}
