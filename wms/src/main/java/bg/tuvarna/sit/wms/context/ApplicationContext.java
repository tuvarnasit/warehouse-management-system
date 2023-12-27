package bg.tuvarna.sit.wms.context;

import bg.tuvarna.sit.wms.factory.ControllerFactory;
import bg.tuvarna.sit.wms.controllers.HomeController;
import bg.tuvarna.sit.wms.controllers.LoginController;
import bg.tuvarna.sit.wms.controllers.RegistrationController;
import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.service.CredentialManagerService;
import bg.tuvarna.sit.wms.service.EncryptionService;
import bg.tuvarna.sit.wms.service.PasswordHashingService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.util.JpaUtil;
import lombok.Getter;

public class ApplicationContext {

  @Getter
  private static final UserService USER_SERVICE =
          new UserService(new UserDao(JpaUtil.getEntityManagerFactory()), new PasswordHashingService());

  @Getter
  private static final EncryptionService ENCRYPTION_SERVICE = new EncryptionService();

  @Getter
  private static final CredentialManagerService CREDENTIAL_MANAGER_SERVICE =
          new CredentialManagerService(ENCRYPTION_SERVICE);

  @Getter
  private static final ControllerFactory CONTROLLER_FACTORY = createControllerFactory();

  private static ControllerFactory createControllerFactory() {

    ControllerFactory factory = new ControllerFactory();
    factory.addController(LoginController.class, () -> new LoginController(USER_SERVICE, CREDENTIAL_MANAGER_SERVICE));
    factory.addController(HomeController.class, () -> new HomeController(USER_SERVICE, CREDENTIAL_MANAGER_SERVICE));
    factory.addController(RegistrationController.class, () -> new RegistrationController(USER_SERVICE));
    return factory;
  }
}
