import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import com.epam.bioinf.variantcaller.helpers.TestHelper;
import htsjdk.samtools.SAMFormatException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparator;
import static org.junit.Assert.assertEquals;

public class SamHandlerTest {

  @Test
  public void samHandlerMustReadCorrectWithOneFile() {
    SamHandler samHandler = getSamHandler("test1.sam");
    long expectedReadsNumber = 10;
    long gotReadsNumber = samHandler.getSamRecords().size();
    assertEquals(gotReadsNumber, expectedReadsNumber);
  }

  @Test
  public void samHandlerMustReadCorrectWithMultipleFiles() {
    SamHandler samHandler = getSamHandler("test1.sam", "test2.sam");
    long expectedReadsNumber = 20;
    long gotReadsNumber = samHandler.getSamRecords().size();
    assertEquals(gotReadsNumber, expectedReadsNumber);
  }

  @Test
  public void samHandlerMustReturnCorrectReadsNumberWithRealExample() {
    SamHandler samHandler = getSamHandler("realSamFileExample.sam");
    Path testFilePath = Paths.get(testFilePath("realSamFileExample.sam"));
    long correctReadsNumber = 1985;

    Map<Path, Long> expectedReadsByPath = Map.of(testFilePath, correctReadsNumber);
    Map<Path, Long> gotReadsByPath = samHandler.countReadsByPath();
    assertEquals(gotReadsByPath, expectedReadsByPath);
  }

  @Test
  public void samHandlerMustReturnCorrectReadsNumberWithOneFile() {
    SamHandler samHandler = getSamHandler("test1.sam");
    Path testFilePath = Paths.get(testFilePath("test1.sam"));
    long correctReadsNumber = 10;

    Map<Path, Long> expectedReadsByPath = Map.of(testFilePath, correctReadsNumber);
    Map<Path, Long> gotReadsByPath = samHandler.countReadsByPath();
    assertEquals(gotReadsByPath, expectedReadsByPath);
  }

  @Test
  public void samHandlerMustReturnCorrectReadsNumberWithMultipleFiles() {
    SamHandler samHandler = getSamHandler("test1.sam", "test2.sam");

    Path firstTestFilePath = Paths.get(testFilePath("test1.sam"));
    Path secondTestFilePath = Paths.get(testFilePath("test2.sam"));
    long correctReadsNumber = 10;

    Map<Path, Long> expectedReadsByPath =
        Map.of(firstTestFilePath, correctReadsNumber, secondTestFilePath, correctReadsNumber);
    Map<Path, Long> gotReadsByPath = samHandler.countReadsByPath();
    assertEquals(gotReadsByPath, expectedReadsByPath);
  }

  @Test(expected = SAMFormatException.class)
  public void samHandlerMustFailIfInvalidOrEmptyFileProvided() {
    getSamHandler("testInvalidReadsFile.sam").countReadsByPath();
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
        "--bed", testFilePath("test1.bed"),
        "--sam", samFilesPaths
    };
  }

}
