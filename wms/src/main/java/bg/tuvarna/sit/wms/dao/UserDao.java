package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data Access Object (DAO) for user operations.
 * Provides an abstraction layer for database operations related to user entities.
 */
public class UserDao {

  private static final Logger LOGGER = LogManager.getLogger(UserDao.class);

  /**
   * Persists a user entity to the database.
   * Handles transaction management and ensures the user is saved within a transaction context.
   *
   * @param user The user entity to persist.
   * @throws UserPersistenceException If there is a persistence error during the saving process.
   */
  public void saveUser(User user) throws UserPersistenceException {

    EntityManager entityManager = JpaUtil.getEntityManager();

    try {
      entityManager.getTransaction().begin();
      entityManager.persist(user);
      entityManager.getTransaction().commit();
    } catch (PersistenceException e) {
      handleTransactionRollback(entityManager);
      throw new UserPersistenceException("Persistence error saving user", e);
    } catch (Exception e) {
      handleTransactionRollback(entityManager);
      throw new UserPersistenceException("Unexpected error saving user", e);
    } finally {
      if (entityManager.isOpen()) {
        entityManager.close();
      }
    }
  }

  /**
   * Handles the rollback of a transaction in case of an error.
   * If the transaction is active, it attempts to roll back the transaction and logs any rollback failures.
   *
   * @param entityManager The entity manager whose transaction needs to be rolled back.
   */
  private void handleTransactionRollback(EntityManager entityManager) {

    try {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
    } catch (Exception e) {
      LOGGER.error("Transaction rollback failed", e);
    }
  }
}
