import static com.epam.bioinf.variantcaller.cmdline.CommandLineParser.CommandLineMessages.*;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandLineArgsrIntegTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() throws IOException {
    String[] invalidTestArgs = {
        "--fasta", integTestFilePath("test1.fasta"),
        "--bed", integTestFilePath("test1.bed"),
        "--sam", integTestFilePath("test1.sam")
    };
    String errorString = launchProcessWithArgs(invalidTestArgs);
    assertTrue(errorString.isEmpty());
  }

  @Test
  public void programMustFailWithInvalidArguments() throws IOException {
    String[] invalidTestArgs = {
        "--fasta", integTestFilePath("test1.fasta") + pathSeparatorChar + integTestFilePath("test2.fasta"),
        "--bed", integTestFilePath("test1.bed"),
        "--sam", integTestFilePath("test1.sam")
    };
    String errorString = launchProcessWithArgs(invalidTestArgs);
    assertEquals("Exception in thread \"main\" java.lang.IllegalArgumentException: " + FASTA_ARGS_COUNT_EXC, errorString);
  }

  /**
   * Launches process and if it fails gets error
   *
   * @param args command line arguments array
   * @return the string which holds error if process creates one or is empty otherwise
   * @throws IOException the exception which is thrown when process fails to launch
   */
  private String launchProcessWithArgs(String[] args) throws IOException {
    List<String> command = new ArrayList<>();
    command.add("java");
    command.add("-jar");
    command.add(PATH_TO_BUILT_JAR.toString());
    command.addAll(Arrays.asList(args));

    ProcessBuilder builder = new ProcessBuilder(command);
    Process p = builder.start();
    BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    String errorString = error.readLine();
    return errorString == null ? "" : errorString;
  }
}
