package bg.tuvarna.sit.wms.factory;

import javafx.util.Callback;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating controller instances. This factory allows for custom controller
 * instantiation logic, particularly useful when controllers need specific dependencies.
 */
public class ControllerFactory implements Callback<Class<?>, Object> {

  private final Map<Class<?>, Supplier<?>> creators = new HashMap<>();

  /**
   * Registers a controller type and its associated creation logic.
   *
   * @param type    The class type of the controller.
   * @param creator A supplier that provides instances of the controller.
   * @param <T>     The type of the controller.
   */
  public <T> void addController(Class<T> type, Supplier<T> creator) {
    creators.put(type, creator);
  }

  /**
   * Called by the FXMLLoader when a controller is needed. The factory uses the registered
   * creator if available; otherwise, it tries to create the controller using its default constructor.
   *
   * @param type The class type of the controller needed.
   * @return A new instance of the controller.
   * @throws RuntimeException If the controller cannot be instantiated.
   */
  @Override
  public Object call(Class<?> type) {

    Supplier<?> creator = creators.get(type);
    if (creator != null) {
      return creator.get();
    } else {
      try {
        return type.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Cannot create controller: " + type.getName(), e);
      }
    }
  }
}

