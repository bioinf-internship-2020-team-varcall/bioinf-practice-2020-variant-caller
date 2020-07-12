import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionBothIntervalOptionsException;
import org.junit.jupiter.api.Test;
import joptsimple.OptionException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static helpers.UnitTestHelper.commonTestFilePath;
import static java.io.File.pathSeparatorChar;
import static org.junit.jupiter.api.Assertions.*;

public class CommandLineParserTest {

  @ParameterizedTest
  @MethodSource("provideArgumentsForMustBeBuiltWithValidParameters")
  public void parserMustBeBuiltWithValidParameters(String[] testParameters) {
    CommandLineParser.parse(getArgs(testParameters));
  }

  @Test
  public void parserMustFailWithExcessParameters() {
    String[] invalidTestArgs = getArgs("--fasta", "--bed", "--sam", "--region");
    assertThrows(RegionBothIntervalOptionsException.class, () ->
        CommandLineParser.parse(invalidTestArgs)
    );
  }

  @Test
  public void parserMustFailWithInvalidParameters() {
    String[] invalidTestArgs = getArgs("-p", "-ap", "-hp");
    assertThrows(OptionException.class, () ->
        CommandLineParser.parse(invalidTestArgs)
    );
  }

  @Test
  public void parserMustReturnCorrectParsedArgumentsWithValidArguments() {
    String[] correctTestArgs = getArgs("--fasta", "--bed", "--sam");
    ParsedArguments result = CommandLineParser.parse(correctTestArgs);
    assertEquals(result.getFastaPath(), Paths.get(commonTestFilePath("test1.fasta")));
    assertEquals(result.getBedPaths().get(0), Paths.get(commonTestFilePath("test1.bed")));
    assertEquals(result.getSamPaths().get(0), Paths.get(commonTestFilePath("test1.sam")));
  }

  @Test
  public void parserMustReturnCorrectParsedArgumentsIfMultipleArgumentsProvided() {
    String[] correctTestArgs = getMultipleArgs();
    ParsedArguments result = CommandLineParser.parse(correctTestArgs);
    Path expectedFastaPath = Paths.get(commonTestFilePath("test1.fasta"));
    List<Path> expectedBedPaths = List.of(
        Paths.get(commonTestFilePath("test1.bed")),
        Paths.get(commonTestFilePath("test2.bed"))
    );
    List<Path> expectedSamPaths = List.of(
        Paths.get(commonTestFilePath("test1.sam")),
        Paths.get(commonTestFilePath("test2.sam"))
    );

    Path parsedFastaPath = result.getFastaPath();
    List<Path> parsedBedPaths = result.getBedPaths();
    List<Path> parsedSamPaths = result.getSamPaths();

    assertEquals(
        expectedFastaPath,
        parsedFastaPath,
        "Expected fasta path is not equal to parsed one"
    );
    assertEquals(
        Set.copyOf(expectedBedPaths),
        Set.copyOf(parsedBedPaths),
        "Unexpected BED file paths"
    );
    assertEquals(
        Set.copyOf(expectedSamPaths),
        Set.copyOf(parsedSamPaths),
        "Unexpected SAM file paths"
    );
  }

  private static Stream<Arguments> provideArgumentsForMustBeBuiltWithValidParameters() {
    return Stream.of(
        Arguments.of((Object) new String[]{"--fasta", "--bed", "--sam"}),
        Arguments.of((Object) new String[]{"--fasta", "--sam"}),
        Arguments.of((Object) new String[]{"--fasta", "--sam", "--region"})
    );
  }

  private String[] getMultipleArgs() {
    return new String[] {
        "--fasta", commonTestFilePath("test1.fasta"),
        "--bed", commonTestFilePath("test1.bed") + pathSeparatorChar + commonTestFilePath("test2.bed"),
        "--sam", commonTestFilePath("test1.sam") + pathSeparatorChar + commonTestFilePath("test2.sam")
    };
  }

  private String[] getArgs(String... keys) {
    List<String> output = new ArrayList<>();
    for (String key : keys) {
      output.add(key);
      output.add(evaluateKey(key));
    }
    return output.toArray(String[]::new);
  }

  private String evaluateKey(String key) {
    switch (key) {
        case ("--fasta"):
          return commonTestFilePath("test1.fasta");
        case ("--sam"):
          return commonTestFilePath("test1.sam");
        case ("--bed"):
          return commonTestFilePath("test1.bed");
        case ("--region"):
          return "chr1 10 20";
        default:
          return null;
    }
  }
}
