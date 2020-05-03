import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import com.epam.bioinf.variantcaller.helpers.TestHelper;
import htsjdk.samtools.SAMFormatException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparator;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamHandlerTest {

  @Test
  public void samHandlerMustReadCorrectWithOneFile() {
    SamHandler samHandler = getSamHandler("test1.sam");
    long expectedReadsNumber = 10;
    long actualReadsNumber = samHandler.getSamRecords().size();
    assertEquals(expectedReadsNumber, actualReadsNumber);
  }

  @Test
  public void samHandlerMustReadCorrectWithMultipleFiles() {
    SamHandler samHandler = getSamHandler("test1.sam", "test2.sam");
    long expectedReadsNumber = 20;
    long actualReadsNumber = samHandler.getSamRecords().size();
    assertEquals(expectedReadsNumber, actualReadsNumber);
  }

  @Test
  public void samHandlerMustFailIfInvalidOrEmptyFileProvided() {
    assertThrows(SAMFormatException.class,
        () -> getSamHandler("testInvalidReadsFile.sam"));
  }

  @Test
  public void samHandlerMustReturnCorrectWithInterval() {
    SamHandler samHandler = getSamHandler("test1.sam");
    final long expected = 4;
    assertEquals(expected, samHandler.getSamRecordsCount());
  }

  private SamHandler getSamHandler(String... samFilesName) {
    String[] testArgs = getArgs(samFilesName);
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    return new SamHandler(parsedArguments);
  }

  private String[] getArgs(String... samFilesNames) {
    String samFilesPaths = Arrays.stream(samFilesNames)
        .map(TestHelper::testFilePath)
        .collect(Collectors.joining(pathSeparator));
    return new String[]{
        "--fasta", testFilePath("test1.fasta"),
        "--sam", samFilesPaths,
        "--region", "chr1 1 556"
    };
  }

}
