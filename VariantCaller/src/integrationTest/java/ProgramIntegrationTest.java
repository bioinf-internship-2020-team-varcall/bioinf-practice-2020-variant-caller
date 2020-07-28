import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    assertTrue(Files.readAllLines(processInfo.errorPath).isEmpty());
    assertEquals(
        Files.readAllLines(intCommonRefTestFilePath("simple_expected_output.txt")).toString(),
        Files.readAllLines(processInfo.outputPath).toString());
    assertEquals(0, processInfo.exitValue);
  }

  //TODO Why is this test running so long?
  //  @Test
  //  public void programMustReturnCorrectContextsForLongSequence()
  //      throws IOException, InterruptedException {
  //    String[] correctTestArgs = {
  //        "--fasta", intCommonTestFilePath("cv1.fasta"),
  //        "--sam", intCommonTestFilePath("cv1_grouped.sam")
  //    };
  //    ProcessInfo processInfo = launchProcessWithArgs(correctTestArgs);
  //    assertEquals(
  //        Files.readAllLines(intCommonRefTestFilePath("cv1.fasta")).toString(),
  //        Files.readAllLines(processInfo.outputPath).toString()
  //    );
  //    assertEquals(0, processInfo.exitValue);
  //  }

  @Test
  public void programMustFailWithInvalidArguments() throws IOException, InterruptedException {
    String[] invalidTestArgs = {
        "--fasta", intCommonTestFilePath("simple1.fasta") + pathSeparatorChar
        + intCommonTestFilePath("simple2.fasta"),
        "--bed", intCommonTestFilePath("simple1.bed"),
        "--sam", intCommonTestFilePath("simple1.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(invalidTestArgs);
    assertFalse(Files.readAllLines(processInfo.outputPath).toString().isEmpty());
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
    BufferedReader error = new BufferedReader(
        new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8)
    );
    BufferedReader output = new BufferedReader(
        new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8)
    );
    p.waitFor();
    Path errorPath = writeToTempFile(error, "error");
    Path outputPath = writeToTempFile(output, "output");
    return new ProcessInfo(p.exitValue(), errorPath, outputPath);
  }

  private Path writeToTempFile(BufferedReader reader, String filename) throws IOException {
    Path tempFile = Files.createTempFile(filename, ".temp");
    String iter = reader.readLine();
    while (iter != null) {
      Files.write(tempFile, (iter + "\n").getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.APPEND);
      iter = reader.readLine();
    }
    reader.close();
    return tempFile;
  }

  private static final class ProcessInfo {
    public int exitValue;
    public Path errorPath;
    public Path outputPath;

    public ProcessInfo(int exitValue, Path errorPath, Path outputPath) {
      this.exitValue = exitValue;
      this.errorPath = errorPath;
      this.outputPath = outputPath;
    }
  }
}
