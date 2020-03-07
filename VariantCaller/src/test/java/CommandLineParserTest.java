import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import org.junit.jupiter.api.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import joptsimple.OptionException;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.TEST_RESOURCES_ROOT;
import static com.epam.bioinf.variantcaller.helpers.TestHelper.separator;
import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static org.junit.rules.ExpectedException.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineParserTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void parserMustBeBuiltWithValidParameters() {
    String[] correctTestArgs = {
        "--fasta", testFilePath(TEST_RESOURCES_ROOT, "test1.fasta"),
        "--bed", testFilePath(TEST_RESOURCES_ROOT, "test1.bed"),
        "--sam", testFilePath(TEST_RESOURCES_ROOT, "test1.sam")
    };
    CommandLineParser.parse(correctTestArgs);
  }

  @Test
  public void parserMustFailWithInvalidParameters() {
    String[] invalidTestArgs = {
        "-p", testFilePath(TEST_RESOURCES_ROOT, "test1.fasta"),
        "-ap", testFilePath(TEST_RESOURCES_ROOT, "test1.bed"),
        "-hp", testFilePath(TEST_RESOURCES_ROOT, "test1.sam")
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
      fail();
    } catch (Exception e) {
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustReturnCorrectParsedArgumentsWithValidArguments() {
    String[] correctTestArgs = {
        "--fasta", testFilePath(TEST_RESOURCES_ROOT, "test1.fasta"),
        "--bed", testFilePath(TEST_RESOURCES_ROOT, "test1.bed"),
        "--sam", testFilePath(TEST_RESOURCES_ROOT, "test1.sam")
    };
    ParsedArguments result = CommandLineParser.parse(correctTestArgs);
    assertEquals(result.getFastaPath(), Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test1.fasta")));
    assertEquals(result.getBedPaths().get(0), Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test1.bed")));
    assertEquals(result.getSamPaths().get(0), Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test1.sam")));
  }

  @Test
  public void parserMustReturnCorrectParsedArgumentsIfMultipleArgumentsProvided() {
    String[] correctTestArgs = {
        "--fasta", testFilePath(TEST_RESOURCES_ROOT, "test1.fasta"),
        "--bed", testFilePath(TEST_RESOURCES_ROOT, "test1.bed") + separator + testFilePath(TEST_RESOURCES_ROOT, "test2.bed"),
        "--sam", testFilePath(TEST_RESOURCES_ROOT, "test1.sam") + separator + testFilePath(TEST_RESOURCES_ROOT, "test2.sam")
    };
    ParsedArguments result = CommandLineParser.parse(correctTestArgs);
    Path expectedFastaPath = Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test1.fasta"));
    List<Path> expectedBedPaths = List.of(
        Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test1.bed")),
        Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test2.bed"))
    );
    List<Path> expectedSamPaths = List.of(
        Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test1.sam")),
        Paths.get(testFilePath(TEST_RESOURCES_ROOT, "test2.sam"))
    );

    Path parsedFastaPath = result.getFastaPath();
    List<Path> parsedBedPaths = result.getBedPaths();
    List<Path> parsedSamPaths = result.getSamPaths();

    assertEquals(expectedFastaPath, parsedFastaPath, "Expected fasta path is not equal to parsed one");
    assertEquals(Set.copyOf(expectedBedPaths), Set.copyOf(parsedBedPaths), "Unexpected BED file paths");
    assertEquals(Set.copyOf(expectedSamPaths), Set.copyOf(parsedSamPaths), "Unexpected SAM file paths");
  }
}
