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
import bg.tuvarna.sit.wms.session.UserSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class for handling user registration.
 * This class manages the process of registering a new user, including input validation,
 * password hashing, and persisting user data.
 */
public class UserService {

  private final UserDao userDao;
  private final PasswordHashingService passwordHashingService;

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);

  public UserService(UserDao userDao, PasswordHashingService passwordHashingService) {

    this.userDao = userDao;
    this.passwordHashingService = passwordHashingService;
  }

  /**
   * Registers a new user based on the provided registration data.
   *
   * @param registrationDto Data Transfer Object containing user registration details.
   * @throws RegistrationException if there is a problem with user registration, such as invalid input or persistence errors.
   */
  public void registerUser(UserRegistrationDto registrationDto) throws RegistrationException {

    if (userDao.findByEmail(registrationDto.getEmail()).isPresent()) {
      throw new RegistrationException("A user with this email already exists.");
    }

    String normalizedPhone = normalizePhoneNumber(registrationDto.getPhone());
    if (userDao.findByPhone(normalizedPhone).isPresent()) {
      throw new RegistrationException("A user with this phone number already exists.");
    }

    User user = createUserFromDto(registrationDto);
    setUserPassword(user, registrationDto.getPassword());
    saveUser(user);
  }

  /**
   * Initializes default user accounts in the system.
   * <p>
   * This method checks for the existence of specific administrator accounts and creates them if they do not exist.
   * This ensures that there are always default administrator accounts available in the system.
   *
   * @throws RegistrationException    If there is a problem with the registration process of the administrators.
   * @throws InvalidKeySpecException  If there is an issue with the specification of the key used in password hashing.
   * @throws NoSuchAlgorithmException If the algorithm specified for password hashing does not exist.
   */
  public void initializeUsers() throws RegistrationException, InvalidKeySpecException, NoSuchAlgorithmException {

    // TODO: Remove the magic values by setting up an external configuration properties
    if (userDao.findByEmail("admin1@example.com").isEmpty()) {
      saveUser(createUser("Admin1", "One", "1234567890", "admin1@example.com", "securepassword1", Role.ADMIN));
    }
    if (userDao.findByEmail("admin2@example.com").isEmpty()) {
      saveUser(createUser("Admin2", "Two", "0987654321", "admin2@example.com", "securepassword2", Role.ADMIN));
    }
    if (userDao.findByEmail("owner1@example.com").isEmpty()) {
      saveUser(createUser("Owner1", "One", "0987654311", "owner1@example.com", "securepassword1", Role.OWNER));
    }
    if (userDao.findByEmail("agent1@example.com").isEmpty()) {
      saveUser(createUser("Agent1", "One", "0987351311", "agent1@example.com", "securepassword1", Role.AGENT));
    }
    if (userDao.findByEmail("agent2@example.com").isEmpty()) {
      saveUser(createUser("Agent2", "Two", "0987254351", "agent2@example.com", "securepassword2", Role.AGENT));
    }
    if (userDao.findByEmail("agent3@example.com").isEmpty()) {
      saveUser(createUser("Agent3", "Three", "0947854511", "agent3@example.com", "securepassword3", Role.AGENT));
    }
    if (userDao.findByEmail("agent4@example.com").isEmpty()) {
      saveUser(createUser("Agent4", "Four", "0987154319", "agent4@example.com", "securepassword4", Role.AGENT));
    }
  }

  /**
   * Attempts to log in a user with the provided email and password.
   *
   * @param email    The email of the user trying to log in.
   * @param password The password of the user.
   * @return true if the login is successful, false otherwise.
   */
  public boolean login(String email, String password) {

    try {
      Optional<User> userOptional = userDao.findByEmail(email);

      if (userOptional.isPresent() && passwordHashingService.validatePassword(password, userDao.getUserPasswordById(userOptional.get().getId()).get())) {
        UserSession.getInstance().setCurrentUser(userOptional.get());
        return true;
      }

      return false;
    } catch (Exception e) {
      LOGGER.error("Login error", e);
      return false;
    }
  }

  /**
   * Creates a User entity from a UserRegistrationDto.
   *
   * @param dto User registration data transfer object.
   * @return A User entity populated with data from the DTO.
   * @throws RegistrationException if the provided role is invalid.
   */
  private User createUserFromDto(UserRegistrationDto dto) throws RegistrationException {

    Optional<User> userOptional = getUserBasedOnRole(dto.getRole());

    if (userOptional.isEmpty()) {
      String errorMessage = "Invalid role provided for user registration.";
      LOGGER.error(errorMessage);
      throw new RegistrationException(errorMessage);
    }

    User user = userOptional.get();
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setRole(Role.valueOf(dto.getRole()));

    return user;
  }

  /**
   * Hashes a password using a secure cryptographic algorithm.
   *
   * @param password The password to hash.
   * @return The hashed password.
   * @throws RegistrationException if there is an error during password hashing.
   */
  private String hashPassword(String password) throws RegistrationException {

    try {
      return passwordHashingService.generateStrongPasswordHash(password);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      String errorMessage = "Error hashing password for user registration.";
      LOGGER.error(errorMessage, e);
      throw new RegistrationException(errorMessage, e);
    }
  }

  /**
   * Sets the user's password after hashing it.
   *
   * @param user     The user whose password is to be set.
   * @param password The user's plaintext password.
   * @throws RegistrationException if there is an error during password hashing.
   */
  private void setUserPassword(User user, String password) throws RegistrationException {

    String hashedPassword = hashPassword(password);
    user.setPassword(hashedPassword);
  }

  /**
   * Saves the user entity to the database.
   *
   * @param user The user entity to save.
   * @throws RegistrationException if there is an error while persisting the user.
   */
  private void saveUser(User user) throws RegistrationException {

    try {
      userDao.saveUser(user);
      LOGGER.info("User saved successfully: " + user.getEmail());
    } catch (UserPersistenceException e) {
      String errorMessage = "Error persisting user during registration.";
      LOGGER.error(errorMessage, e);
      throw new RegistrationException(errorMessage, e);
    }
  }

  /**
   * Retrieves a user entity based on the specified role.
   *
   * @param role The role of the user.
   * @return An Optional containing the User entity if the role is valid, otherwise an empty Optional.
   */
  private Optional<User> getUserBasedOnRole(String role) {

    return switch (role.toUpperCase()) {
      case "OWNER" -> Optional.of(new Owner());
      case "AGENT" -> Optional.of(new Agent());
      default -> Optional.empty();
    };
  }

  private User createUser(String firstName, String lastName, String phone, String email,
                           String rawPassword, Role role)
      throws InvalidKeySpecException, NoSuchAlgorithmException, RegistrationException {

    if(role == null) {
      throw new RegistrationException("Role must not be null");
    }

    User user = switch (role) {
      case ADMIN -> new User();
      case OWNER -> new Owner();
      case AGENT -> new Agent();
    };

    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setPhone(phone);
    user.setEmail(email);
    user.setPassword(passwordHashingService.generateStrongPasswordHash(rawPassword));
    user.setRole(role);

    return user;
  }

  private String normalizePhoneNumber(String phone) {

    if (phone.startsWith("+359")) {
      return "0" + phone.substring(4);
    }

    return phone;
  }

}
