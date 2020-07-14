import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;
import com.epam.bioinf.variantcaller.exceptions.handlers.region.RegionReadingException;
import com.epam.bioinf.variantcaller.handlers.IntervalsHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helpers.UnitTestHelper.*;
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
    ParsedArguments parsedArguments = getParsedArguments("--bed", "malformed.bed");
    assertThrows(RegionReadingException.class,
        () -> IntervalsHandler.getIntervals(parsedArguments));
  }

  @Test
  void intervalsHandlerMustFailIfOneOfTheFilesCanNotBeDecoded() {
    ParsedArguments parsedArguments =
        getParsedArguments("--bed", "test1.bed", "test2.bed", "malformed.bed");
    assertThrows(RegionReadingException.class,
        () -> IntervalsHandler.getIntervals(parsedArguments));
  }

  @Test
  void intervalsHandlerMustReturnCorrectOverlappingIntervalsSize() {
    final long expectedSize = 3;
    ParsedArguments parsedArguments = getParsedArguments("--bed", "overlapping.bed");
    assertEquals(expectedSize, IntervalsHandler.getIntervals(parsedArguments).size());
  }

  private static Stream<Arguments> provideArgumentsForExpectedIntervalsSize() {
    return Stream.of(
        Arguments.of(1, new String[]{"--region", "chr1 12 123"}),
        Arguments.of(5, new String[]{"--bed", "test1.bed"}),
        Arguments.of(6, new String[]{"--bed", "test1.bed", "test2.bed"})
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
        "--fasta", commonTestFilePath("test1.fasta"),
        key, keyValue,
        "--sam", commonTestFilePath("test1.sam")
    };
  }

  private String collectPathsToStringWithoutKey(String... input) {
    return Arrays.stream(input)
        .skip(1)
        .map(filename ->
            checkIfCommon(filename) ?
                commonTestFilePath(filename) : intervalsCasesTestFilePath(filename)
        )
        .collect(Collectors.joining(pathSeparator));
  }
}
