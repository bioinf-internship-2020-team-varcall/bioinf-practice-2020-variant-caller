import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.IntervalsHandler;

import com.epam.bioinf.variantcaller.helpers.TestHelper;
import htsjdk.tribble.bed.BEDFeature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparator;
import static org.junit.jupiter.api.Assertions.*;

public class IntervalsHandlerTest {

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalsNumberWithCmdLineInput() {
    final long expectedIntervalsNumber = 1;
    IntervalsHandler testHandler = getIntervalsHandler("chr1 12 123");
    assertEquals(expectedIntervalsNumber, testHandler.getIntervals().stream().count());
  }

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalsNumberWithSingleFile() {
    final long expectedIntervalsNumber = 7;
    IntervalsHandler testHandler = getIntervalsHandler("test1.bed");
    assertEquals(expectedIntervalsNumber, testHandler.getIntervals().stream().count());
  }

  @Test void intervalsHandlerMustReturnCorrectIntervalsNumberWithMultipleFiles() {
    final long expectedIntervalsNumber = 16;
    IntervalsHandler testHandler = getIntervalsHandler("test1.bed", "test2.bed");
    assertEquals(expectedIntervalsNumber, testHandler.getIntervals().stream().count());
  }

  private IntervalsHandler getIntervalsHandler(String... bedFilesNames) {
    String[] correctTestArgs = getArgs(bedFilesNames);
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    return new IntervalsHandler(parsedArguments);
  }

  private String[] getArgs(String... input) {
    if (input[0].split(" ").length == 3) {
      return new String[]{
          "--fasta", testFilePath("test1.fasta"),
          "--region", input[0],
          "--sam", testFilePath("test1.sam")
      };
    }
    String bedFilesPaths = Arrays.stream(input)
        .map(TestHelper::testFilePath)
        .collect(Collectors.joining(pathSeparator));
    return new String[]{
        "--fasta", testFilePath("test1.fasta"),
        "--bed", bedFilesPaths,
        "--sam", testFilePath("test1.sam")
    };
  }
}
