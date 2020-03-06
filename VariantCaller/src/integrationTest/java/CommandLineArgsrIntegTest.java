import static com.epam.bioinf.variantcaller.cmdline.CommandLineParser.CommandLineMessages.*;

import com.epam.bioinf.variantcaller.helpers.GradleHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandLineArgsrIntegTest {
  private static final Path INTEG_TEST_RECOURCES_ROOT = Path.of("src", "integrationTest", "resources").toAbsolutePath();
  private final char separator = File.pathSeparatorChar;

  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() throws IOException {
    String[] invalidTestArgs = {
        "'--fasta'", "'" + testFilePath("test1.fasta") + "'",
        "'--bed'", "'" + testFilePath("test1.bed") + "'",
        "'--sam'", "'" + testFilePath("test1.sam") + "'"
    };
    String joinedInvalidArgs = String.join(",", invalidTestArgs);
    String errorString = launchProcessWithArgs(joinedInvalidArgs);
    assertTrue(errorString.isEmpty());
  }

  @Test
  public void programMustFailWithInvalidArguments() throws IOException {
    String[] invalidTestArgs = {
        "'--fasta'", "'" + testFilePath("test1.fasta") + separator + testFilePath("test2.fasta") + "'",
        "'--bed'", "'" + testFilePath("test1.bed") + "'",
        "'--sam'", "'" + testFilePath("test1.sam") + "'"
    };
    String joinedInvalidArgs = String.join(",", invalidTestArgs);
    String errorString = launchProcessWithArgs(joinedInvalidArgs);
    assertEquals("Exception in thread \"main\" java.lang.IllegalArgumentException: " + FASTA_ARGS_COUNT_EXC, errorString);
  }

  /**
   * Launches process and if it fails gets error
   *
   * @param joinedArgs the prepared command line arguments joined to string and separated by comma
   * @return the string which holds error if process creates one or is empty otherwise
   * @throws IOException the exception which is thrown when process fails to launch
   */
  private String launchProcessWithArgs(String joinedArgs) throws IOException {
    String gradleWrapperCallCommand = GradleHelper.getGradleExecutable();
    String command = Paths.get(System.getProperty("user.dir"), gradleWrapperCallCommand).toString() + " run -PtestArgs=[" + joinedArgs + "]";
    Runtime r = Runtime.getRuntime();
    Process p = r.exec(command);
    BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    String errorString = error.readLine();
    return errorString == null ? "" : errorString;
  }

  private static String testFilePath(String fileName) {
    return INTEG_TEST_RECOURCES_ROOT.resolve(fileName).toString().replace("\\", "/");
  }
}
