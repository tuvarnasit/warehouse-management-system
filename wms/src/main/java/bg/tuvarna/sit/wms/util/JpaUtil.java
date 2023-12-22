package bg.tuvarna.sit.wms.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

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

  @Getter
  private static final EntityManagerFactory entityManagerFactory;

  static {

    Map<String, String> env = System.getenv();
    Map<String, Object> configOverrides = new HashMap<>();
    configOverrides.put("hibernate.connection.url", env.get("DB_CONNECTION_URL"));
    configOverrides.put("hibernate.connection.username", env.get("DB_USERNAME"));
    configOverrides.put("hibernate.connection.password", env.get("DB_PASSWORD"));

    entityManagerFactory = Persistence.createEntityManagerFactory(env.get("PERSISTENCE_NAME"), configOverrides);
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
