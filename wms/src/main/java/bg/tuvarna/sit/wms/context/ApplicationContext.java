package bg.tuvarna.sit.wms.context;

import bg.tuvarna.sit.wms.controllers.MyReviewsController;
import bg.tuvarna.sit.wms.controllers.RentalAgreementController;
import bg.tuvarna.sit.wms.controllers.WarehouseControlPanelController;
import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.dao.ReviewDao;
import bg.tuvarna.sit.wms.dao.WarehouseDAO;
import bg.tuvarna.sit.wms.factory.ControllerFactory;
import bg.tuvarna.sit.wms.controllers.HomeController;
import bg.tuvarna.sit.wms.controllers.LoginController;
import bg.tuvarna.sit.wms.controllers.RegistrationController;
import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.service.CityService;
import bg.tuvarna.sit.wms.service.CountryService;
import bg.tuvarna.sit.wms.service.CredentialManagerService;
import bg.tuvarna.sit.wms.service.EncryptionService;
import bg.tuvarna.sit.wms.service.PasswordHashingService;
import bg.tuvarna.sit.wms.service.ReviewService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.util.JpaUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Getter;

public class ApplicationContext {

  private static final UserDao USER_DAO =
          new UserDao(JpaUtil.getEntityManagerFactory());

  private static final ReviewDao REVIEW_DAO =
          new ReviewDao(JpaUtil.getEntityManagerFactory());

  @Getter
  private static final UserService USER_SERVICE =
          new UserService(USER_DAO, new PasswordHashingService());

  @Getter
  private static final EncryptionService ENCRYPTION_SERVICE = new EncryptionService();

  @Getter
  private static final CredentialManagerService CREDENTIAL_MANAGER_SERVICE =
          new CredentialManagerService(ENCRYPTION_SERVICE);

  @Getter
  private static final CityService CITY_SERVICE =
          new CityService(new CityDAO(JpaUtil.getEntityManagerFactory()));

  @Getter
  private static final CountryService COUNTRY_SERVICE =
          new CountryService(new CountryDAO(JpaUtil.getEntityManagerFactory()));

  @Getter
  private static final WarehouseService WAREHOUSE_SERVICE =
          new WarehouseService(new WarehouseDAO(JpaUtil.getEntityManagerFactory()), COUNTRY_SERVICE, CITY_SERVICE);

  @Getter
  private static final ReviewService REVIEW_SERVICE =
          new ReviewService(USER_DAO, REVIEW_DAO);

  @Getter
  private static final ControllerFactory CONTROLLER_FACTORY = createControllerFactory();

  private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

  private static ControllerFactory createControllerFactory() {

    ControllerFactory factory = new ControllerFactory();
    factory.addController(LoginController.class, () -> new LoginController(USER_SERVICE, CREDENTIAL_MANAGER_SERVICE));
    factory.addController(HomeController.class, () -> new HomeController(USER_SERVICE, CREDENTIAL_MANAGER_SERVICE));
    factory.addController(RegistrationController.class, () -> new RegistrationController(USER_SERVICE));
    factory.addController(WarehouseControlPanelController.class, () -> new WarehouseControlPanelController(WAREHOUSE_SERVICE));
    factory.addController(MyReviewsController.class, () -> new MyReviewsController(REVIEW_SERVICE, SCHEDULED_EXECUTOR_SERVICE));
    factory.addController(RentalAgreementController.class, () -> new RentalAgreementController(WAREHOUSE_SERVICE, REVIEW_SERVICE));

    return factory;
  }
}
