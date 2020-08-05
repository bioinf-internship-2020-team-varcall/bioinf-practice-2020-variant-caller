package helpers;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Class contains constants and helper methods for unit and integration tests
 */
public final class UnitTestHelper {
  public static final Path TEST_RESOURCES_ROOT = Path
      .of("src", "test", "resources")
      .toAbsolutePath();

  private UnitTestHelper() {
    // restrict instantiation
  }

  /**
   * Method returns a path to a unit test resource with given filename
   */
  public static String commonTestFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve("common").resolve(filename).toString();
  }

  /**
   * Method returns a path to unit test resources
   * with given filename related to special intervals test cases
   */
  public static String intervalsCasesTestFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve("intervals").resolve(filename).toString();
  }

  /**
   * Method returns a path to unit test resources
   * with given filename related to special sam test cases
   */
  public static String samCasesTestFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve("sam").resolve(filename).toString();
  }

  /**
   * Method returns a path to unit test reference resources
   */
  public static Path callerRefFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve("caller").resolve("ref").resolve(filename);
  }

  /**
   * Method returns a path to unit test resources
   * with given filename related to special caller test cases
   */
  public static String callerTestFilePath(String filename) {
    return TEST_RESOURCES_ROOT.resolve("caller").resolve(filename).toString();
  }

  public static boolean checkIfCommon(String filename) {
    return Pattern.matches("test[0-9].*", filename);
  }
}
