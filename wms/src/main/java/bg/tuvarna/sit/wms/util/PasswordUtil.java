package bg.tuvarna.sit.wms.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Utility class for handling password hashing.
 * Provides methods to generate a strong password hash using PBKDF2 algorithm.
 */
public class PasswordUtil {

  /**
   * Generates a strong hash for a given password.
   * <p>
   * Uses PBKDF2 with HMAC SHA1 to hash the password with a salt. The number of iterations is set to 1000.
   *
   * @param password The password to hash.
   * @return A strong hash of the password.
   * @throws NoSuchAlgorithmException if the specified algorithm is not available.
   * @throws InvalidKeySpecException  if the specified key specification is inappropriate for this secret-key factory.
   */
  public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
    int iterations = 1000;
    char[] chars = password.toCharArray();
    byte[] salt = getSalt();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = skf.generateSecret(spec).getEncoded();
    return iterations + ":" + toHex(salt) + ":" + toHex(hash);
  }

  /**
   * Generates a random salt for use in password hashing.
   *
   * @return A 16-byte random salt.
   * @throws NoSuchAlgorithmException if the SHA1PRNG algorithm is not available.
   */
  private static byte[] getSalt() throws NoSuchAlgorithmException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }

  /**
   * Converts a byte array to a hexadecimal string.
   *
   * @param array The byte array to convert.
   * @return A hexadecimal string representing the byte array.
   */
  private static String toHex(byte[] array) {
    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }
}
