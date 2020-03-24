package com.epam.bioinf.variantcaller.helpers;

import java.nio.file.Path;

public final class TestHelper {
  public static Path INTEG_TEST_RECOURCES_ROOT = Path.of("src", "integrationTest", "resources")
      .toAbsolutePath();
  public static Path TEST_RESOURCES_ROOT = Path.of("src", "test", "resources").toAbsolutePath();
  public static Path PATH_TO_BUILT_JAR = Path.of("build", "libs", "VariantCaller.jar");

  private TestHelper() {
    // restrict instantiation
  }

  public static String integTestFilePath(String filename) {
    return INTEG_TEST_RECOURCES_ROOT.resolve(filename).toString();
  }

  public static String testFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve(filename).toString();
  }
}
