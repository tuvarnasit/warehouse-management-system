package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Tenant;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import static bg.tuvarna.sit.wms.util.PasswordUtil.generateStrongPasswordHash;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserService {

  private final UserDao userDao = new UserDao();

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);

  public void registerUser(UserRegistrationDto registrationDto) throws RegistrationException {

    Optional<User> userOptional = getUserBasedOnRole(registrationDto.getRole());

    if (userOptional.isEmpty()) {
      String errorMessage = "Invalid role provided for user registration.";
      LOGGER.error(errorMessage);
      throw new RegistrationException(errorMessage);
    }

    User user = userOptional.get();
    user.setFirstName(registrationDto.getFirstName());
    user.setLastName(registrationDto.getLastName());
    user.setEmail(registrationDto.getEmail());

    try {
      String hashedPassword = generateStrongPasswordHash(registrationDto.getPassword());
      user.setPassword(hashedPassword);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      String errorMessage = "Error hashing password for user registration.";
      LOGGER.error(errorMessage, e);
      throw new RegistrationException(errorMessage, e);
    }

    user.setPhone(registrationDto.getPhone());
    user.setRole(Role.valueOf(registrationDto.getRole()));

    try {
      userDao.saveUser(user);
      LOGGER.info("User saved successfully: " + user.getEmail());
    } catch (UserPersistenceException e) {
      String errorMessage = "Error persisting user during registration.";
      LOGGER.error(errorMessage, e);
      throw new RegistrationException(errorMessage, e);
    }
  }

  private Optional<User> getUserBasedOnRole(String role) {

    return switch (role.toUpperCase()) {
      case "OWNER" -> Optional.of(new Owner());
      case "AGENT" -> Optional.of(new Agent());
      case "TENANT" -> Optional.of(new Tenant());
      default -> Optional.empty();
    };
  }
}
