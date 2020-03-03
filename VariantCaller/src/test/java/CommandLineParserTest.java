import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import joptsimple.OptionSet;
import org.junit.jupiter.api.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import joptsimple.OptionException;

import static org.junit.rules.ExpectedException.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineParserTest {
  private final String TEST_RESOURCES_ROOT = Paths.get("src/test/resources").toAbsolutePath().toString();

  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void parserMustAcceptValidParameters() {
    String[] correctTestArgs = {
        "--fasta", TEST_RESOURCES_ROOT + "/test1.fasta",
        "--bed", TEST_RESOURCES_ROOT + "/test1.bed",
        "--sam", TEST_RESOURCES_ROOT + "/test1.sam"
    };
    CommandLineParser parser = CommandLineParser.build(correctTestArgs);
    OptionSet parserOptions = parser.getOptions();

    assertTrue(parserOptions.has("fasta"), "Parser must accept --fasta parameter");
    assertTrue(parserOptions.has("bed"), "Parser must accept --bed parameter");
    assertTrue(parserOptions.has("sam"), "Parser must accept --sam parameter");
  }

  @Test
  public void parserMustBeBuiltWithValidParameters() {
    String[] correctTestArgs = {
        "--fasta", TEST_RESOURCES_ROOT + "/test1.fasta",
        "--bed", TEST_RESOURCES_ROOT + "/test1.bed",
        "--sam", TEST_RESOURCES_ROOT + "/test1.sam"
    };
    CommandLineParser.build(correctTestArgs);
  }

  @Test
  public void parserMustFailWithInvalidParameters() {
    String[] invalidTestArgs = {
        "-p", TEST_RESOURCES_ROOT + "/test1.fasta",
        "-ap", TEST_RESOURCES_ROOT + "/test1.bed",
        "-hp", TEST_RESOURCES_ROOT + "/test1.sam"
    };
    try {
      CommandLineParser.build(invalidTestArgs);
      fail();
    } catch (Exception e) {
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustReturnCorrectParsedArgumentsWithValidArguments() {
    String[] correctTestArgs = {
        "--fasta", TEST_RESOURCES_ROOT + "/test1.fasta",
        "--bed", TEST_RESOURCES_ROOT + "/test1.bed",
        "--sam", TEST_RESOURCES_ROOT + "/test1.sam"
    };
    ParsedArguments result = CommandLineParser.build(correctTestArgs).getParsedArguments();
    assertEquals(result.getFastaPath(), Paths.get(TEST_RESOURCES_ROOT, "/test1.fasta"));
    assertEquals(result.getBedPaths().get(0), Paths.get(TEST_RESOURCES_ROOT, "/test1.bed"));
    assertEquals(result.getSamPaths().get(0), Paths.get(TEST_RESOURCES_ROOT, "/test1.sam"));
  }

  @Test
  public void parserMustReturnCorrectParsedArgumentsIfMultipleArgumentsProvided() {
    String[] correctTestArgs = {
        "--fasta", TEST_RESOURCES_ROOT + "/test1.fasta",
        "--bed", TEST_RESOURCES_ROOT + "/test1.bed" + ":" + TEST_RESOURCES_ROOT + "/test2.bed",
        "--sam", TEST_RESOURCES_ROOT + "/test1.sam" + ":" + TEST_RESOURCES_ROOT + "/test2.sam"
    };
    ParsedArguments result = CommandLineParser.build(correctTestArgs).getParsedArguments();
    String expectedFastaPath = TEST_RESOURCES_ROOT + "/test1.fasta";
    List<Path> expectedBedPaths = List.of(Paths.get(TEST_RESOURCES_ROOT, "/test1.bed"), Paths.get(TEST_RESOURCES_ROOT, "/test2.bed"));
    List<Path> expectedSamPaths = List.of(Paths.get(TEST_RESOURCES_ROOT, "/test1.sam"), Paths.get(TEST_RESOURCES_ROOT, "/test2.sam"));

    String parsedFastaPath = result.getFastaPath().toString();
    List<Path> parsedBedPaths = result.getBedPaths();
    List<Path> parsedSamPaths = result.getSamPaths();

    assertEquals(expectedFastaPath, parsedFastaPath, "Expected fasta path is not equal to parsed one");
    assertEquals(Set.copyOf(expectedBedPaths), Set.copyOf(parsedBedPaths), "Unexpected BED file paths");
    assertEquals(Set.copyOf(expectedSamPaths), Set.copyOf(parsedSamPaths), "Unexpected SAM file paths");
  }
}
