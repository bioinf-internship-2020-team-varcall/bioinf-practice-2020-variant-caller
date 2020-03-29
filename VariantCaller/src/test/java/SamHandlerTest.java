import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMFormatException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparatorChar;
import static org.junit.Assert.assertEquals;

public class SamHandlerTest {

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
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam") + pathSeparatorChar +
        testFilePath("test2.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    SamHandler samHandler = new SamHandler(parsedArguments);

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

  @Test(expected = IllegalArgumentException.class)
  public void samHandlerMustFailIfFileContainsOnlyOneRead() {
    getSamHandler("testFileWithOneRead.sam").countReadsByPath();
  }

  private SamHandler getSamHandler(String samFileName) {
    String[] testArgs = getArgs(samFileName);
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    return new SamHandler(parsedArguments);
  }

  private String[] getArgs(String samFileName) {
    return new String[]{
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath(samFileName)
    };
  }

//TODO есть ли идентичные строчки(риды) в файлах?
//TODO передается 2 и более файла. сделать под такой кейс getArgs case
}
