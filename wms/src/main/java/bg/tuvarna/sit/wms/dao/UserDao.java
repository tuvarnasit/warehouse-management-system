package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDao {

  private static final Logger LOGGER = LogManager.getLogger(UserDao.class);

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
