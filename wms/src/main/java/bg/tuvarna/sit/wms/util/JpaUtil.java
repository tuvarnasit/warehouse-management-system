package bg.tuvarna.sit.wms.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing JPA EntityManagerFactory and providing EntityManager instances.
 * <p>
 * This class encapsulates the setup of the EntityManagerFactory using configuration overrides
 * derived from environment variables. It provides a static method to obtain EntityManager
 * instances for interacting with the persistence context.
 * </p>
 *
 * @author Yavor Chamov
 * @version 1.0.0
 */
public class JpaUtil {

  /**
   * The singleton instance of EntityManagerFactory for the application.
   */
  private static final EntityManagerFactory entityManagerFactory;

  static {
    Map<String, String> env = System.getenv();
    Map<String, Object> configOverrides = new HashMap<>();
    configOverrides.put("hibernate.connection.url", env.get("DB_CONNECTION_URL"));
    configOverrides.put("hibernate.connection.username", env.get("DB_USERNAME"));
    configOverrides.put("hibernate.connection.password", env.get("DB_PASSWORD"));

    entityManagerFactory = Persistence.createEntityManagerFactory("wms", configOverrides);
  }

  /**
   * Provides an EntityManager from the factory to interact with the persistence context.
   *
   * @return A new instance of EntityManager.
   */
  public static EntityManager getEntityManager() {
    return entityManagerFactory.createEntityManager();
  }

  /**
   * Closes the EntityManagerFactory when it's no longer needed, such as when the application
   * is shutting down. This method ensures that all resources are released properly.
   */
  public static void close() {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }
}

