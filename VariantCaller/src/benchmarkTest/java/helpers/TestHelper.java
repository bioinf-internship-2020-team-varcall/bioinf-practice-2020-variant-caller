package helpers;

import java.nio.file.Path;

/**
 * Class contains constants and helper methods for unit and integration tests
 */
public final class TestHelper {
  public static final Path TEST_RESOURCES_ROOT = Path
      .of("src", "benchmarkTest", "resources")
      .toAbsolutePath();

  private TestHelper() {
    // restrict instantiation
  }

  /**
   * Method returns a path to benchmark test resources
   * with given filename related to special caller test cases
   */
  public static String callerTestFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve("caller").resolve(filename).toString();
  }

}
