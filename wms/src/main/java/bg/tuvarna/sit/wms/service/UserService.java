package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Tenant;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import static bg.tuvarna.sit.wms.util.PasswordUtil.generateStrongPasswordHash;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserService {

  private final UserDao userDao = new UserDao();

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);

  public void registerUser(UserRegistrationDto registrationDto) {

    Optional<User> userOptional = getUserBasedOnRole(registrationDto.getRole());

    userOptional.ifPresentOrElse(
            user -> {
              user.setFirstName(registrationDto.getFirstName());
              user.setLastName(registrationDto.getLastName());
              user.setEmail(registrationDto.getEmail());
              try {
                user.setPassword(generateStrongPasswordHash(registrationDto.getPassword()));
              } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                LOGGER.error("Error hashing password", e);
                throw new RuntimeException(e);
              }
              user.setPhone(registrationDto.getPhone());
              user.setRole(Role.valueOf(registrationDto.getRole()));
              userDao.saveUser(user);
            },
            () -> {
              LOGGER.error("Invalid role");
            }
    );
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
