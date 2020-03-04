import static com.epam.bioinf.variantcaller.cmdline.CommandLineParser.CommandLineMessages.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class CommandLineArgsrIntegTest {
  private final String INTEG_TEST_RECOURCES_ROOT = Paths.get("src/integrationTest/resources").toAbsolutePath().toString();

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
        "'--fasta'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.fasta:" + INTEG_TEST_RECOURCES_ROOT + "/test2.fasta'",
        "'--bed'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.bed'",
        "'--sam'", "'" + INTEG_TEST_RECOURCES_ROOT + "/test1.sam'"
    };
    String joinedInvalidArgs = String.join(",", invalidTestArgs);
    String errorString = launchProcessWithArgs(joinedInvalidArgs);
    assertEquals("Exception in thread \"main\" java.lang.IllegalArgumentException: " + FASTA_ARGS_COUNT_EXC, errorString);
  }

  //method returns an error string which is got from launched process
  private String launchProcessWithArgs(String joinedArgs) throws IOException {
    String command = System.getProperty("user.dir") + "/gradlew run -PtestArgs=[" + joinedArgs + "]";
    Runtime r = Runtime.getRuntime();
    Process p = r.exec(command);
    BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    return error.readLine();
  }
}
