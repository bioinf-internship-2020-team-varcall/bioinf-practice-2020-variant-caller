package com.epam.bioinf.variantcaller.helpers;

import java.nio.file.Path;

public final class TestHelper {
  public static final Path INTEG_TEST_RECOURCES_ROOT = Path
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

  public static String integTestFilePath(String filename) {
    return INTEG_TEST_RECOURCES_ROOT.resolve(filename).toString();
  }

  public static String testFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve(filename).toString();
  }
}
