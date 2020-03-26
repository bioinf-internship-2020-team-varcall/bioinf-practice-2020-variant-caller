import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMFormatException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparatorChar;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;

public class SamHandlerTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void samHandlerMustReturnCorrectReadsNumberWithOneFile() {
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    SamHandler samHandler = new SamHandler(parsedArguments.getSamPaths());
    Path testFilePath = Paths.get(testFilePath("test1.sam"));

    Map<Path, Long> expectedReadsByPath = Map.of(testFilePath, 10L);
    Map<Path, Long> gotReadsByPath = samHandler.countReadsByPath();
    assertEquals(gotReadsByPath, expectedReadsByPath);
  }

  @Test
  public void samHandlerMustReturnCorrectReadsNumberWithMultipleFiles() {
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam") + pathSeparatorChar +
        testFilePath("test2.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    SamHandler samHandler = new SamHandler(parsedArguments.getSamPaths());

    Path firstTestFilePath = Paths.get(testFilePath("test1.sam"));
    Path secondTestFilePath = Paths.get(testFilePath("test2.sam"));

    Map<Path, Long> expectedReadsByPath = Map.of(firstTestFilePath, 10L, secondTestFilePath, 10L);
    Map<Path, Long> gotReadsByPath = samHandler.countReadsByPath();
    assertEquals(gotReadsByPath, expectedReadsByPath);
  }

  @Test(expected = SAMFormatException.class)
  public void samHandlerMustFailIfInvalidOrEmptyFileProvided() {
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("testInvalidReadsFile.sam")};
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    new SamHandler(parsedArguments.getSamPaths()).countReadsByPath();
  }

  @Test(expected = IllegalArgumentException.class)
  public void samHandlerMustFailIfEmptyFilesListProvided() {
    List<Path> emptyList = Collections.emptyList();
    new SamHandler(emptyList);
  }

  @Test(expected = IllegalArgumentException.class)
  public void samHandlerMustFailIfFileContainsOnlyOneRead() {
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("testFileWithOneRead.sam")};
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    new SamHandler(parsedArguments.getSamPaths()).countReadsByPath();
  }

  //TODO есть ли идентичные строчки(риды) в файлах?
}
