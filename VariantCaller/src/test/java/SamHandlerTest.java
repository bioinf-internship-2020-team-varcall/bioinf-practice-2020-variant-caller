import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamNoRelatedReadsException;
import com.epam.bioinf.variantcaller.handlers.IntervalsHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import com.epam.bioinf.variantcaller.helpers.TestHelper;
import htsjdk.samtools.SAMFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparator;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamHandlerTest {
  private static final Pattern fileSplit = Pattern.compile(" ");

  @Test
  public void samHandlerMustReadCorrectWithOneFile() {
    SamHandler samHandler = getSamHandler("test1.sam");
    long expectedReadsNumber = 10;
    long actualReadsNumber = samHandler.getSamRecords().size();
    assertEquals(expectedReadsNumber, actualReadsNumber);
  }

  @Test
  public void samHandlerMustReadCorrectWithMultipleFiles() {
    SamHandler samHandler = getSamHandler("test1.sam test2.sam");
    long expectedReadsNumber = 20;
    long actualReadsNumber = samHandler.getSamRecords().size();
    assertEquals(expectedReadsNumber, actualReadsNumber);
  }

  @Test
  public void samHandlerMustFailIfInvalidOrEmptyFileProvided() {
    assertThrows(SAMFormatException.class,
        () -> getSamHandler("testInvalidReadsFile.sam"));
  }

  @ParameterizedTest
  @MethodSource("provideArgumentsForCorrectNumberOfFilteredReads")
  public void samHandlerMustReturnCorrectNumberOfFilteredReads(int expectedSize,
      String samFiles, String intervalKey, String intervalData) {
    SamHandler samHandler = getSamHandler(samFiles, intervalKey, intervalData);
    Assertions.assertEquals(expectedSize, samHandler.getSamRecordsCount());
  }

  @Test
  public void samHandlerMustFailIfNoReadsRelateToProvidedIntervals() {
    assertThrows(SamNoRelatedReadsException.class,
        () -> getSamHandler("test1.sam", "--region", "chr5 1 556"));
  }

  private static Stream<Arguments> provideArgumentsForCorrectNumberOfFilteredReads() {
    return Stream.of(
        Arguments.of(4, "test1.sam", "--region", "chr1 1 556"),
        Arguments.of(6, "test1.sam", "--bed", "test1.bed")
//        multiple files filtered by single interval
//        multiple files filtered by multiple intervals
    );
  }

  private SamHandler getSamHandler(String... input) {
    String[] testArgs = getArgs(input);
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    return new SamHandler(parsedArguments);
  }

  private String[] getArgs(String... input) {
    String samFilesPaths = collectPaths(input[0]);
    if (input.length == 1) {
      return new String[] {
          "--fasta", testFilePath("test1.fasta"),
          "--sam", samFilesPaths
      };
    }
    String key = input[1];
    String keyValue = "";
    if (key == "--region") {
      keyValue = input[2];
    } else {
      keyValue = collectPaths(input[2]);
    }
    return new String[]{
        "--fasta", testFilePath("test1.fasta"),
        "--sam", samFilesPaths,
        key, keyValue
    };
  }

  private String collectPaths(String input) {
    return Arrays.stream(fileSplit.split(input))
        .map(TestHelper::testFilePath)
        .collect(Collectors.joining(pathSeparator));
  }
}
