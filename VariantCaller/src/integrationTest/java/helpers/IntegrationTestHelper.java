package helpers;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class IntegrationTestHelper {
  public static final Path INTEGRATION_TEST_RESOURCES_ROOT = Path
      .of("src", "integrationTest", "resources")
      .toAbsolutePath();

  public static final Path PATH_TO_BUILT_JAR = Path
      .of("build", "libs", "VariantCaller.jar")
      .toAbsolutePath();

  /**
   * Returns a path to a integration test resource with given filename.
   */
  public static String intCommonTestFilePath(String filename) {
    return INTEGRATION_TEST_RESOURCES_ROOT.resolve("common").resolve(filename).toString();
  }

  /**
   * Returns a path to a integration test ref resource with given filename.
   */
  public static Path intCommonRefTestFilePath(String filename) {
    return INTEGRATION_TEST_RESOURCES_ROOT.resolve("common").resolve("ref").resolve(filename);
  }

  public static boolean checkIfCommon(String filename) {
    return Pattern.matches("simple[0-9].*", filename);
  }
}
