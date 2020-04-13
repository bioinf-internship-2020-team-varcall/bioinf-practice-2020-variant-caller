import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.*;

import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import joptsimple.OptionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.PATH_TO_BUILT_JAR;
import static com.epam.bioinf.variantcaller.helpers.TestHelper.integTestFilePath;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

import java.io.BufferedReader;

import static java.io.File.pathSeparatorChar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandLineArgsrIntegTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() throws IOException, InterruptedException {
    String[] invalidTestArgs = {
        "--fasta", integTestFilePath("test1.fasta"),
        "--bed", integTestFilePath("test1.bed"),
        "--sam", integTestFilePath("test1.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(invalidTestArgs);
    assertTrue(processInfo.errorString.isEmpty());
    assertEquals(0, processInfo.exitValue);
  }

  @Test
  public void programMustFailWithInvalidArguments() throws IOException, InterruptedException {
    String[] invalidTestArgs = {
        "--fasta", integTestFilePath("test1.fasta") + pathSeparatorChar
          + integTestFilePath("test2.fasta"),
        "--bed", integTestFilePath("test1.bed"),
        "--sam", integTestFilePath("test1.sam")
    };
    ProcessInfo processInfo = launchProcessWithArgs(invalidTestArgs);
    assertEquals("Exception in thread \"main\" "
        + "com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaArgsSizeException: "
        + FASTA_ARGS_COUNT_EXC, processInfo.errorString);
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
