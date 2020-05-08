import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;
import com.epam.bioinf.variantcaller.exceptions.handlers.region.RegionReadingException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionInvalidException;
import com.epam.bioinf.variantcaller.handlers.IntervalsHandler;
import com.epam.bioinf.variantcaller.helpers.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntervalsHandlerTest {

  @ParameterizedTest
  @MethodSource("provideArgumentsForExpectedIntervalsSize")
  public void intervalsHandlerMustReturnCorrectIntervalsSize(int expectedSize,
      String[] flagData) {
    assertEquals(expectedSize, IntervalsHandler.getIntervals(getParsedArguments(flagData)).size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"chr1 a 123", "chr1 -12 123", "chr1 10 6"})
  void intervalsHandlerMustFailIfRegionPointsAreIncorrect(String testData) {
    ParsedArguments parsedArguments = getParsedArguments("--region", testData);
    assertThrows(RegionHandlerException.class,
        () -> IntervalsHandler.getIntervals(parsedArguments));
  }

  @Test
  void intervalsHandlerMustFailIfFileCanNotBeDecoded() {
    ParsedArguments parsedArguments = getParsedArguments("--bed", "test3_malformed.bed");
    assertThrows(RegionReadingException.class,
        () -> IntervalsHandler.getIntervals(parsedArguments));
  }

  @Test
  void intervalsHandlerMustFailIfOneOfTheFilesCanNotBeDecoded() {
    ParsedArguments parsedArguments =
        getParsedArguments("--bed", "test1.bed", "test2.bed", "test3_malformed.bed");
    assertThrows(RegionReadingException.class,
        () -> IntervalsHandler.getIntervals(parsedArguments));
  }

  private static Stream<Arguments> provideArgumentsForExpectedIntervalsSize() {
    return Stream.of(
        Arguments.of(1, new String[]{"--region", "chr1 12 123"}),
        Arguments.of(7, new String[]{"--bed", "test1.bed"}),
        Arguments.of(16, new String[]{"--bed", "test1.bed", "test2.bed"})
    );
  }

  private ParsedArguments getParsedArguments(String... arguments) {
    String[] correctTestArgs = getArgs(arguments);
    return CommandLineParser.parse(correctTestArgs);
  }

  private String[] getArgs(String... input) {
    String key = input[0];
    String keyValue = key.equals("--region") ? input[1] : collectPathsToStringWithoutKey(input);
    return new String[]{
        "--fasta", testFilePath("test1.fasta"),
        key, keyValue,
        "--sam", testFilePath("test1.sam")
    };
  }

  private String collectPathsToStringWithoutKey(String... input) {
    return Arrays.stream(input)
        .skip(1)
        .map(TestHelper::testFilePath)
        .collect(Collectors.joining(pathSeparator));
  }
}
