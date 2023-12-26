package bg.tuvarna.sit.wms.util;

import bg.tuvarna.sit.wms.session.KeyUtil;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Path;

class KeyUtilTest {

  @TempDir
  Path tempDir;

  @Test
  void testSaveAndLoadSecretKey() throws Exception {

    SecretKey originalKey = KeyGenerator.getInstance("AES").generateKey();
    Path keyFile = tempDir.resolve("testkey.key");
    KeyUtil.saveSecretKey(originalKey, keyFile.toString());
    SecretKey loadedKey = KeyUtil.loadSecretKey(keyFile.toString(), "AES");

    assertArrayEquals(originalKey.getEncoded(), loadedKey.getEncoded(),
            "The loaded key should match the original key");
  }

  @Test
  void testLoadSecretKeyWithNonExistentFile() {

    Path keyFile = tempDir.resolve("nonexistent.key");
    assertThrows(IOException.class, () -> KeyUtil.loadSecretKey(keyFile.toString(), "AES"),
            "Loading from a non-existent file should throw an IOException");
  }
}
