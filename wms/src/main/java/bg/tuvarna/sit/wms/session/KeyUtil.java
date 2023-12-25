package bg.tuvarna.sit.wms.session;

import java.io.IOException;
import java.nio.file.Path;
import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for saving and loading secret keys.
 */
public class KeyUtil {

  /**
   * Saves a {@link SecretKey} to a file.
   *
   * @param secretKey The secret key to be saved.
   * @param filename  The name of the file to save the key to.
   * @throws IOException If an I/O error occurs writing to the file.
   */
  public static void saveSecretKey(SecretKey secretKey, String filename) throws IOException {

    Path path = Paths.get(filename);
    byte[] keyBytes = secretKey.getEncoded();
    Files.write(path, Base64.getEncoder().encode(keyBytes));
  }

  /**
   * Loads a {@link SecretKey} from a file.
   *
   * @param filename  The name of the file from which to load the key.
   * @param algorithm The algorithm associated with the secret key (e.g., "AES").
   * @return The loaded secret key.
   * @throws IOException If an I/O error occurs reading from the file.
   */
  public static SecretKey loadSecretKey(String filename, String algorithm) throws IOException {

    byte[] keyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(filename)));
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm);
  }
}
