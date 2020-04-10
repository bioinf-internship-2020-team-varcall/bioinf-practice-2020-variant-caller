import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.IntervalsHandler;

import com.epam.bioinf.variantcaller.helpers.TestHelper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparator;
import static org.junit.jupiter.api.Assertions.*;

public class IntervalsHandlerTest {

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalsNumberWithCmdLineInput() {
    final long expectedIntervalsNumber = 1;
    IntervalsHandler intervalsHandler = getIntervalsHandler("--region", "chr1 12 123");
    assertEquals(expectedIntervalsNumber, intervalsHandler.getIntervals().size());
  }

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalsNumberWithSingleFile() {
    final long expectedIntervalsNumber = 7;
    IntervalsHandler intervalsHandler = getIntervalsHandler("--bed", "test1.bed");
    assertEquals(expectedIntervalsNumber, intervalsHandler.getIntervals().size());
  }

  @Test
  void intervalsHandlerMustReturnCorrectIntervalsNumberWithMultipleFiles() {
    final long expectedIntervalsNumber = 16;
    IntervalsHandler intervalsHandler = getIntervalsHandler("--bed", "test1.bed", "test2.bed");
    assertEquals(expectedIntervalsNumber, intervalsHandler.getIntervals().size());
  }

  @Test
  void intervalsHandlerMustFailIfRegionPointsAreIncorrect() {
    assertThrows(IllegalArgumentException.class,
        () -> getIntervalsHandler("--region", "chr1 a 123"));
    assertThrows(IllegalArgumentException.class,
        () -> getIntervalsHandler("--region", "chr1 -12 123"));
    assertThrows(IllegalArgumentException.class,
        () -> getIntervalsHandler("--region", "chr1 10 6"));
  }

  @Test
  void intervalsHandlerMustFailIfFileCanNotBeDecoded() {
    assertThrows(RuntimeException.class,
        () -> getIntervalsHandler("--bed", "test3_malformed.bed"));
  }

  @Test
  void intervalsHandlerMustFailIfOneOfTheFilesCanNotBeDecoded() {
    assertThrows(RuntimeException.class,
        () -> getIntervalsHandler("--bed", "test1.bed", "test2.bed", "test3_malformed.bed"));
  }

  private IntervalsHandler getIntervalsHandler(String... arguments) {
    String[] correctTestArgs = getArgs(arguments);
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    return new IntervalsHandler(parsedArguments);
  }

  private String[] getArgs(String... input) {
    String key = input[0];
    String keyValue = "";
    if (key == "--region") {
      keyValue = input[1];
    } else {
      keyValue = collectPathsToStringWithoutKey(input);
    }
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
