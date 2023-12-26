package bg.tuvarna.sit.wms.service;

import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class EncryptionServiceTest {

  private final EncryptionService encryptionService = new EncryptionService();

  @Test
  public void testEncryptDecrypt() throws Exception {

    String originalString = "Hello, World!";
    SecretKey key = encryptionService.generateKey();

    String encryptedString = encryptionService.encrypt(originalString, key);
    assertNotNull(encryptedString, "Encrypted string should not be null");

    String decryptedString = encryptionService.decrypt(encryptedString, key);
    assertEquals(originalString, decryptedString, "Decrypted string should match original");
  }

  @Test
  public void testDecryptWithWrongKey() throws Exception {

    String originalString = "Hello, World!";
    SecretKey key = encryptionService.generateKey();
    SecretKey wrongKey = encryptionService.generateKey();

    String encryptedString = encryptionService.encrypt(originalString, key);

    assertThrows(BadPaddingException.class, () -> {
      encryptionService.decrypt(encryptedString, wrongKey);
    }, "Decrypting with the wrong key should throw an exception");
  }
}
