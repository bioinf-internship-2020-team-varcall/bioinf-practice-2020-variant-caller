import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helpers.IntegrationTestHelper.PATH_TO_BUILT_JAR;
import static helpers.IntegrationTestHelper.intCommonTestFilePath;
import static java.io.File.pathSeparatorChar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.rules.ExpectedException.none;

public class CommandLineArgsrIntegTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() throws IOException, InterruptedException {
    String[] invalidTestArgs = {
        "--fasta", intCommonTestFilePath("simple1.fasta"),
        "--bed", intCommonTestFilePath("simple1.bed"),
        "--sam", intCommonTestFilePath("simple1.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(invalidTestArgs);
    assertTrue(processInfo.errorString.isEmpty(), processInfo.errorString);
    assertEquals(0, processInfo.exitValue);
  }

  @Test
  public void programMustFailWithInvalidArguments() throws IOException, InterruptedException {
    String[] invalidTestArgs = {
        "--fasta", intCommonTestFilePath("simple1.fasta") + pathSeparatorChar
          + intCommonTestFilePath("simple2.fasta"),
        "--bed", intCommonTestFilePath("simple1.bed"),
        "--sam", intCommonTestFilePath("simple1.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(invalidTestArgs);
    assertEquals("Exception in thread \"main\" "
            + "com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaArgsSizeException: "
            + "Multiple or no paths to '.fasta' files were presented, must be 1",
        processInfo.errorString);
    assertEquals(1, processInfo.exitValue);
  }

  /**
   * Launches process and if it fails gets error
   *
   * @param args command line arguments array
   * @return the string which holds error if process creates one or is empty otherwise
   * @throws IOException the exception which is thrown when process fails to launch
   */
  private ProcessInfo launchProcessWithArgs(String[] args) throws
      IOException,
      InterruptedException {
    List<String> command = new ArrayList<>();
    command.add("java");
    command.add("-jar");
    command.add(PATH_TO_BUILT_JAR.toString());
    command.addAll(Arrays.asList(args));

    ProcessBuilder builder = new ProcessBuilder(command);
    Process p = builder.start();
    BufferedReader error = new BufferedReader(
        new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8)
    );
    p.waitFor();
    String errorLine = error.readLine();
    error.close();
    return new ProcessInfo(p.exitValue(), errorLine);
  }

  private static final class ProcessInfo {
    public int exitValue;
    public String errorString;

    public ProcessInfo(int exitValue, String errorString) {
      this.exitValue = exitValue;
      this.errorString = errorString == null ? "" : errorString;
    }
  }
}
