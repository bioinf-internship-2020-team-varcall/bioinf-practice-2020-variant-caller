package com.epam.bioinf.variantcaller.helpers;

import java.nio.file.Path;

/**
 * Class contains constants and helper methods for unit and integration tests
 */
public final class TestHelper {
  public static final Path INTEGRATION_TEST_RESOURCES_ROOT = Path
      .of("src", "integrationTest", "resources")
      .toAbsolutePath();
  public static final Path TEST_RESOURCES_ROOT = Path
      .of("src", "test", "resources")
      .toAbsolutePath();
  public static final Path PATH_TO_BUILT_JAR = Path
      .of("build", "libs", "VariantCaller.jar")
      .toAbsolutePath();

  private TestHelper() {
    // restrict instantiation
  }

  /**
   * Method returns a path to a integration test resource with given filename
   */
  public static String integTestFilePath(String filename) {
    return INTEGRATION_TEST_RESOURCES_ROOT.resolve(filename).toString();
  }

  /**
   * Method returns a path to a unit test resource with given filename
   */
  public static String testFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve(filename).toString();
  }
}
