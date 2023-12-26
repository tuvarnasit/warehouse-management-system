package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.exceptions.CredentialSavingException;
import bg.tuvarna.sit.wms.session.Credentials;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class CredentialManagerServiceTest {

  @Mock
  private EncryptionService encryptionService;
  @Mock
  private SecretKey key;

  private CredentialManagerService credentialManager;

  @BeforeEach
  void setUp(@TempDir Path tempDir) throws NoSuchPaddingException, IllegalBlockSizeException,
          NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    MockitoAnnotations.openMocks(this);

    when(encryptionService.encrypt(anyString(), any(SecretKey.class)))
            .thenAnswer(i -> "encrypted_" + i.getArguments()[0]);
    when(encryptionService.decrypt(anyString(), any(SecretKey.class)))
            .thenAnswer(i -> ((String) i.getArguments()[0]).replace("encrypted_", ""));

    credentialManager = new CredentialManagerService(encryptionService);
    credentialManager.setCREDENTIALS_FILE(tempDir.resolve("credentials.txt").toString());
    credentialManager.setKey(key);
  }

  @Test
  void testSaveCredentials() throws CredentialSavingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

    credentialManager.saveCredentials("test@example.com", "password123");

    verify(encryptionService, times(1)).encrypt("test@example.com", key);
    verify(encryptionService, times(1)).encrypt("password123", key);
  }

  @Test
  void testLoadCredentials() throws CredentialSavingException {

    credentialManager.saveCredentials("test@example.com", "password123");

    Optional<Credentials> loadedCredentials = credentialManager.loadCredentials();

    assertTrue(loadedCredentials.isPresent());
    assertEquals("test@example.com", loadedCredentials.get().getEmail());
    assertEquals("password123", loadedCredentials.get().getPassword());
  }

  @Test
  void testLoadCredentialsWithInvalidFile() {

    System.setProperty("SSO_CREDENTIALS_FILE", "non_existent_file.txt");

    Optional<Credentials> loadedCredentials = credentialManager.loadCredentials();

    assertFalse(loadedCredentials.isPresent());
  }
}
