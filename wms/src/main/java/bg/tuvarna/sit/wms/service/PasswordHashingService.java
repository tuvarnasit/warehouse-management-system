package bg.tuvarna.sit.wms.service;

import java.util.Arrays;
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
public class PasswordHashingService {

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
  String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
    int iterations = 1000;
    char[] chars = password.toCharArray();
    byte[] salt = getSalt();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = skf.generateSecret(spec).getEncoded();
    return iterations + ":" + toHex(salt) + ":" + toHex(hash);
  }

  /**
   * Converts a byte array to a hexadecimal string.
   *
   * @param array The byte array to convert.
   * @return A hexadecimal string representing the byte array.
   */
  String toHex(byte[] array) {

    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }

  boolean validatePassword(String originalPassword, String storedPasswordHash)
          throws NoSuchAlgorithmException, InvalidKeySpecException {

    String[] parts = storedPasswordHash.split(":");
    int iterations = Integer.parseInt(parts[0]);
    byte[] salt = fromHex(parts[1]);
    byte[] hash = fromHex(parts[2]);

    PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] testHash = skf.generateSecret(spec).getEncoded();

    return Arrays.equals(hash, testHash);
  }

  /**
   * Generates a random salt for use in password hashing.
   *
   * @return A 16-byte random salt.
   * @throws NoSuchAlgorithmException if the SHA1PRNG algorithm is not available.
   */
  private byte[] getSalt() throws NoSuchAlgorithmException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }

  /**
   * Converts a hexadecimal string to a byte array.
   *
   * @param hex The hexadecimal string to convert.
   * @return A byte array representing the hexadecimal string.
   */
  private byte[] fromHex(String hex) {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
  }
}
