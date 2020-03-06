import static com.epam.bioinf.variantcaller.cmdline.CommandLineParser.CommandLineMessages.*;

import com.epam.bioinf.variantcaller.helpers.OsCheck;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class CommandLineArgsrIntegTest {
  private final String INTEG_TEST_RECOURCES_ROOT = Paths.get("src/integrationTest/resources").toAbsolutePath().toString().replace("\\", "/");
  private final char separator = File.pathSeparatorChar;

  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() throws IOException {
    String[] invalidTestArgs = {
        "'--fasta'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.fasta",
        "'--bed'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.bed'",
        "'--sam'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.sam'"
    };
    String joinedInvalidArgs = String.join(",", invalidTestArgs);
    String errorString = launchProcessWithArgs(joinedInvalidArgs);
    assertTrue(errorString.isEmpty());
  }

  @Test
  public void programMustFailWithInvalidArguments() throws IOException {
    String[] invalidTestArgs = {
        "'--fasta'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.fasta" + separator + INTEG_TEST_RECOURCES_ROOT + "/test2.fasta'",
        "'--bed'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.bed'",
        "'--sam'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.sam'"
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
    String gradleWrapperCallCommand = OsCheck.getGradleExecutable();
    String command = Paths.get(System.getProperty("user.dir"), "/" + gradleWrapperCallCommand).toString() + " run -PtestArgs=[" + joinedArgs + "]";
    Runtime r = Runtime.getRuntime();
    Process p = r.exec(command);
    BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    String errorString = error.readLine();
    return errorString == null ? "" : errorString;
  }
}
