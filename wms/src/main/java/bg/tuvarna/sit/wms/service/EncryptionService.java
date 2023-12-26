package bg.tuvarna.sit.wms.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Service for encrypting and decrypting strings using the AES algorithm.
 */
public class EncryptionService {

  private static final String ALGORITHM = "AES";
  private static final int KEY_SIZE = 128;

  /**
   * Generates a new AES SecretKey.
   *
   * @return A newly generated SecretKey.
   * @throws NoSuchAlgorithmException If the AES algorithm is not available.
   */
  public SecretKey generateKey() throws NoSuchAlgorithmException {

    KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
    keyGenerator.init(KEY_SIZE);
    return keyGenerator.generateKey();
  }

  /**
   * Encrypts a string using AES encryption with the provided key.
   *
   * @param value The string to encrypt.
   * @param key   The SecretKey used for encryption.
   * @return The encrypted string, encoded in Base64.
   * @throws NoSuchPaddingException    If padding mechanism is not available.
   * @throws NoSuchAlgorithmException  If AES algorithm is not available.
   * @throws InvalidKeyException       If the given key is inappropriate for initializing this cipher.
   * @throws IllegalBlockSizeException If the length of data provided to the block cipher is incorrect.
   * @throws BadPaddingException       If a particular padding mechanism is expected for the input data.
   */
  public String encrypt(String value, SecretKey key) throws NoSuchPaddingException,
          NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encryptedByteValue);
  }

  /**
   * Decrypts a Base64 encoded string using AES decryption with the provided key.
   *
   * @param value The Base64 encoded string to decrypt.
   * @param key   The SecretKey used for decryption.
   * @return The decrypted string.
   * @throws NoSuchPaddingException    If padding mechanism is not available.
   * @throws NoSuchAlgorithmException  If AES algorithm is not available.
   * @throws InvalidKeyException       If the given key is inappropriate for initializing this cipher.
   * @throws IllegalBlockSizeException If the length of data provided to the block cipher is incorrect.
   * @throws BadPaddingException       If a particular padding mechanism is expected for the input data.
   */
  public String decrypt(String value, SecretKey key) throws NoSuchPaddingException,
          NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

    byte[] decodedBytes = Base64.getDecoder().decode(value);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
  }
}

