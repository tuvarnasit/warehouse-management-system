package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.exceptions.CredentialSavingException;
import bg.tuvarna.sit.wms.session.Credentials;
import bg.tuvarna.sit.wms.session.KeyUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the saving and loading of encrypted user credentials for Single Sign-On (SSO) functionality.
 */
public class CredentialManagerService {

  private static final Logger LOGGER = LogManager.getLogger(CredentialManagerService.class);

  @Setter(value = AccessLevel.PACKAGE)
  private String CREDENTIALS_FILE = System.getenv().get("SSO_CREDENTIALS_FILE");

  @Setter(value = AccessLevel.PACKAGE)
  private SecretKey key;

  private final EncryptionService encryptionService;

  /**
   * Constructs a CredentialManagerService with a provided EncryptionService.
   * Attempts to load the encryption key from a file. If the key cannot be loaded,
   * a RuntimeException is thrown.
   *
   * @param encryptionService The encryption service used for encrypting and decrypting credentials.
   * @throws RuntimeException If an IOException occurs while loading the encryption key.
   */
  public CredentialManagerService(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
    try {
      key = KeyUtil.loadSecretKey("encryption.key", "AES");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Saves encrypted user credentials to a file for SSO.
   * The email and password are encrypted before saving.
   *
   * @param email    The user's email to save.
   * @param password The user's password to save.
   * @throws CredentialSavingException If an error occurs during the encryption or file writing process.
   */
  public void saveCredentials(String email, String password) throws CredentialSavingException {
    try {
      String encryptedEmail = encryptionService.encrypt(email, key);
      String encryptedPassword = encryptionService.encrypt(password, key);
      String timestamp = Long.toString(Instant.now().getEpochSecond());
      Files.write(Paths.get(CREDENTIALS_FILE),
              (encryptedEmail + "\n" + encryptedPassword + "\n" + timestamp).getBytes());
    } catch (Exception e) {
      throw new CredentialSavingException("Failed to save credentials", e);
    }
  }

  /**
   * Loads and decrypts user credentials from a file.
   * If the credentials are older than 30 minutes, they are considered expired and not returned.
   *
   * @return An Optional containing the decrypted credentials if they exist and are valid, or an empty Optional otherwise.
   */
  public Optional<Credentials> loadCredentials() {
    try {
      List<String> lines = Files.readAllLines(Paths.get(CREDENTIALS_FILE));

      if (lines.size() < 3) {
        return Optional.empty();
      }

      String decryptedEmail = encryptionService.decrypt(lines.get(0), key);
      String decryptedPassword = encryptionService.decrypt(lines.get(1), key);
      long timestamp = Long.parseLong(lines.get(2));

      if (Instant.now().getEpochSecond() - timestamp > 1800) { // 1800 seconds = 30 minutes
        return Optional.empty();
      }

      return Optional.of(new Credentials(decryptedEmail, decryptedPassword));
    } catch (IOException | NoSuchAlgorithmException | InvalidKeyException |
             IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
      LOGGER.error("Error loading SSO credentials", e);
      return Optional.empty();
    }
  }
}

