package com.epam.bioinf.variantcaller.helpers;

import java.io.File;
import java.nio.file.Path;

public final class TestHelper {
  public static Path INTEG_TEST_RECOURCES_ROOT = Path.of("src", "integrationTest", "resources").toAbsolutePath();
  public static Path TEST_RESOURCES_ROOT = Path.of("src", "test", "resources").toAbsolutePath();
  public static char separator = File.pathSeparatorChar;

  private TestHelper() {
    // restrict instantiation
  }

  public static String testFilePath(Path recourcesRoot, String filename) {
    if (recourcesRoot == INTEG_TEST_RECOURCES_ROOT)
      return recourcesRoot.resolve(filename).toString().replace("\\", "/");
    return recourcesRoot.resolve(filename).toString();
  }
}
