import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.IntegrationTestHelper.*;
import static java.io.File.pathSeparatorChar;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

public class ProgramIntegrationTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() throws IOException, InterruptedException {
    String[] correctTestArgs = {
        "--fasta", intCommonTestFilePath("simple1.fasta"),
        "--bed", intCommonTestFilePath("simple1.bed"),
        "--sam", intCommonTestFilePath("simple1.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(correctTestArgs);
    assertTrue(processInfo.error.isEmpty());
    assertEquals(
        Files.readAllLines(intCommonRefTestFilePath("simple_expected_output.txt")).toString(),
        processInfo.output.toString()
    );
    assertEquals(0, processInfo.exitValue);
  }

  @Test
  public void programMustReturnCorrectContextsForLongSequence()
      throws IOException, InterruptedException {
    String[] correctTestArgs = {
        "--fasta", intCommonTestFilePath("cv1.fasta"),
        "--sam", intCommonTestFilePath("cv1_grouped.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(correctTestArgs);
    assertEquals(
        Files.readAllLines(intCommonRefTestFilePath("cv1_expected_contexts.txt")).toString(),
        processInfo.output.toString()
    );
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
    assertFalse(processInfo.error.toString().isEmpty());
    assertEquals(1, processInfo.exitValue);
  }

  /**
   * Launches process and if it fails gets error
   *
   * @param args command line arguments array
   * @return the ProcessInfo object which contains paths to error and output files
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
    try (
        BufferedReader error = new BufferedReader(
            new InputStreamReader(p.getErrorStream(), Charset.defaultCharset())
        );
        BufferedReader output = new BufferedReader(
            new InputStreamReader(p.getInputStream(), Charset.defaultCharset())
        );
    ) {
      List<String> outputBuffer = new ArrayList<>();
      String line;
      while ((line = output.readLine()) != null) {
        outputBuffer.add(line);
      }
      p.waitFor();
      return new ProcessInfo(
          p.exitValue(),
          error.lines().collect(Collectors.toList()),
          outputBuffer
      );
    }
  }

  private static final class ProcessInfo {
    public int exitValue;
    public List<String> error;
    public List<String> output;

    public ProcessInfo(int exitValue, List<String> error, List<String> output) {
      this.exitValue = exitValue;
      this.error = error;
      this.output = output;
    }
  }
}
