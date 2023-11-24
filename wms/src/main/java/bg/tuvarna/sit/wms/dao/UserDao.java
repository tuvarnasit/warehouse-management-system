package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDao {

  private static final Logger LOGGER = LogManager.getLogger(UserDao.class);

  public void saveUser(User user) {

    EntityManager entityManager = JpaUtil.getEntityManager();

    try {
      entityManager.getTransaction().begin();
      entityManager.persist(user);
      entityManager.getTransaction().commit();
      LOGGER.info("User saved successfully: " + user.getEmail());
    } catch (Exception e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      LOGGER.error("Error saving user: " + user.getEmail(), e);
    } finally {
      entityManager.close();
    }
  }
}
