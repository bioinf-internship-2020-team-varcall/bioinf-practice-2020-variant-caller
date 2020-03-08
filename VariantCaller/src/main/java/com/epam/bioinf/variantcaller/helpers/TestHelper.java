package com.epam.bioinf.variantcaller.helpers;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestHelper {
  public static Path INTEG_TEST_RECOURCES_ROOT = Path.of("src", "integrationTest", "resources").toAbsolutePath();
  public static Path TEST_RESOURCES_ROOT = Path.of("src", "test", "resources").toAbsolutePath();

  private TestHelper() {
    // restrict instantiation
  }

  public static String integTestFilePath(String filename) {
    return INTEG_TEST_RECOURCES_ROOT.resolve(filename).toString().replace("\\", "/");
  }

  public static String testFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve(filename).toString();
  }

  public static Path getGradleExecutable() {
    String wrapperCallFileName = System.getProperty("os.name").toLowerCase().contains("win") ? "gradlew.bat" : "gradlew";
    return Paths.get(System.getProperty("user.dir"), wrapperCallFileName);
  }
}
