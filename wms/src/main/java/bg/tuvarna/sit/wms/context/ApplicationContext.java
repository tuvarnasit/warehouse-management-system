package bg.tuvarna.sit.wms.context;

import bg.tuvarna.sit.wms.controllers.IncomingRequestsController;
import bg.tuvarna.sit.wms.controllers.WarehouseRentalController;
import bg.tuvarna.sit.wms.controllers.WarehouseControlPanelController;
import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.dao.RentalAgreementDAO;
import bg.tuvarna.sit.wms.dao.WarehouseDAO;
import bg.tuvarna.sit.wms.dao.WarehouseRentalRequestDAO;
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
import bg.tuvarna.sit.wms.service.RentalRequestService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.service.WarehouseService;
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
  private static final CityService CITY_SERVICE =
          new CityService(new CityDAO(JpaUtil.getEntityManagerFactory()));

  @Getter
  private static final CountryService COUNTRY_SERVICE =
          new CountryService(new CountryDAO(JpaUtil.getEntityManagerFactory()));

  @Getter
  private static final WarehouseService WAREHOUSE_SERVICE =
          new WarehouseService(new WarehouseDAO(JpaUtil.getEntityManagerFactory()), COUNTRY_SERVICE, CITY_SERVICE);

  @Getter
  private static final RentalRequestService RENTAL_REQUEST_SERVICE =
          new RentalRequestService(new WarehouseRentalRequestDAO(JpaUtil.getEntityManagerFactory()), WAREHOUSE_SERVICE, new UserDao(JpaUtil.getEntityManagerFactory()));

  @Getter
  private static final RentalAgreementDAO RENTAL_AGREEMENT_DAO =
          new RentalAgreementDAO(JpaUtil.getEntityManagerFactory());

  @Getter
  private static final ControllerFactory CONTROLLER_FACTORY = createControllerFactory();

  private static ControllerFactory createControllerFactory() {

    ControllerFactory factory = new ControllerFactory();
    factory.addController(LoginController.class, () -> new LoginController(USER_SERVICE, CREDENTIAL_MANAGER_SERVICE));
    factory.addController(HomeController.class, () -> new HomeController(USER_SERVICE, CREDENTIAL_MANAGER_SERVICE));
    factory.addController(RegistrationController.class, () -> new RegistrationController(USER_SERVICE));
    factory.addController(WarehouseControlPanelController.class, () -> new WarehouseControlPanelController(WAREHOUSE_SERVICE));
    factory.addController(WarehouseRentalController.class, () -> new WarehouseRentalController(RENTAL_REQUEST_SERVICE, WAREHOUSE_SERVICE));
    factory.addController(IncomingRequestsController.class, () -> new IncomingRequestsController(RENTAL_REQUEST_SERVICE));

    return factory;
  }
}
