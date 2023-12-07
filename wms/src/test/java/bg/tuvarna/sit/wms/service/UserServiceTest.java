package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

  @Mock
  private UserDao userDao;

  @Mock
  private PasswordHashingService passwordHashingService;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void registerUserAgent_ValidUser_ShouldSaveUser() throws RegistrationException, UserPersistenceException {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName("John");
    registrationDto.setLastName("Doe");
    registrationDto.setEmail("john.doe@example.com");
    registrationDto.setPassword("Password123!");
    registrationDto.setPhone("1234567890");
    registrationDto.setRole("AGENT");

    userService.registerUser(registrationDto);

    verify(userDao, times(1)).saveUser(any(User.class));
  }

  @Test
  void registerUserTenant_ValidUser_ShouldSaveUser() throws RegistrationException, UserPersistenceException {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName("John");
    registrationDto.setLastName("Doe");
    registrationDto.setEmail("john.doe@example.com");
    registrationDto.setPassword("Password123!");
    registrationDto.setPhone("1234567890");
    registrationDto.setRole("TENANT");

    userService.registerUser(registrationDto);

    verify(userDao, times(1)).saveUser(any(User.class));
  }

  @Test
  void registerUserOwner_ValidUser_ShouldSaveUser() throws RegistrationException, UserPersistenceException {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName("John");
    registrationDto.setLastName("Doe");
    registrationDto.setEmail("john.doe@example.com");
    registrationDto.setPassword("Password123!");
    registrationDto.setPhone("1234567890");
    registrationDto.setRole("OWNER");

    userService.registerUser(registrationDto);

    verify(userDao, times(1)).saveUser(any(User.class));
  }

  @Test
  void registerUser_InvalidRole_ShouldThrowException() {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setRole("INVALID_ROLE");

    assertThrows(RegistrationException.class, () -> userService.registerUser(registrationDto));
  }

  @Test
  void saveUser_WhenDaoThrowsException_ShouldThrowRegistrationException() throws UserPersistenceException {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName("John");
    registrationDto.setLastName("Doe");
    registrationDto.setEmail("john.doe@example.com");
    registrationDto.setPassword("Password123!");
    registrationDto.setPhone("1234567890");
    registrationDto.setRole("AGENT");

    doThrow(UserPersistenceException.class).when(userDao).saveUser(any(User.class));

    RegistrationException thrown = assertThrows(
            RegistrationException.class,
            () -> userService.registerUser(registrationDto),
            "Expected saveUser to throw RegistrationException, but it didn't"
    );

    assertTrue(thrown.getMessage().contains("Error persisting user during registration."));
    verify(userDao, times(1)).saveUser(any(User.class));
  }

  @Test
  void hashPassword_WhenHashingFailsWithNoSuchAlgorithmException_ShouldThrowRegistrationException() throws InvalidKeySpecException, NoSuchAlgorithmException {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName("John");
    registrationDto.setLastName("Doe");
    registrationDto.setEmail("john.doe@example.com");
    registrationDto.setPassword("Password123!");
    registrationDto.setPhone("1234567890");
    registrationDto.setRole("AGENT");

    when(passwordHashingService.generateStrongPasswordHash(anyString()))
            .thenThrow(NoSuchAlgorithmException.class);

    assertThrows(RegistrationException.class, () -> userService.registerUser(registrationDto));
  }

  @Test
  void hashPassword_WhenHashingFailsWithInvalidKeySpecException_ShouldThrowRegistrationException() throws InvalidKeySpecException, NoSuchAlgorithmException {

    UserRegistrationDto registrationDto = new UserRegistrationDto();
    registrationDto.setFirstName("John");
    registrationDto.setLastName("Doe");
    registrationDto.setEmail("john.doe@example.com");
    registrationDto.setPassword("Password123!");
    registrationDto.setPhone("1234567890");
    registrationDto.setRole("AGENT");

    when(passwordHashingService.generateStrongPasswordHash(anyString()))
            .thenThrow(InvalidKeySpecException.class);

    assertThrows(RegistrationException.class, () -> userService.registerUser(registrationDto));
  }
}