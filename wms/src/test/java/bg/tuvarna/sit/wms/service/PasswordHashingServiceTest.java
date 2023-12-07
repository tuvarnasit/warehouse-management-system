package bg.tuvarna.sit.wms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordHashingServiceTest {

  private PasswordHashingService passwordHashingService;

  @BeforeEach
  void setUp() {
    passwordHashingService = new PasswordHashingService();
  }

  @Test
  void generateStrongPasswordHash_ShouldReturnNonEmptyString() throws Exception {

    String password = "TestPassword";

    String hashedPassword = passwordHashingService.generateStrongPasswordHash(password);

    assertNotNull(hashedPassword);
    assertFalse(hashedPassword.isEmpty());
  }

  @Test
  void toHex_ByteArrayWithLeadingZeros_ShouldPadResult() {

    byte[] byteArrayWithLeadingZeros = new byte[]{0x01, 0x23, 0x45};
    String hexResult = passwordHashingService.toHex(byteArrayWithLeadingZeros);

    assertEquals("012345", hexResult);
  }
}
