import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMFormatException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static java.io.File.pathSeparatorChar;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;

public class SamHandlerTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void samHandlerMustReturnCorrectReadsNumberWithExamples() {
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam") + pathSeparatorChar +
        testFilePath("test2.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    SamHandler samHandler = new SamHandler(parsedArguments.getSamPaths());

    Path firstTestFilePath = Paths.get(testFilePath("test1.sam"));
    Path secondTestFilePath = Paths.get(testFilePath("test2.sam"));

    Map<Path, Long> expectedReadsByPath = Map.of(firstTestFilePath, 10L, secondTestFilePath, 10L);
    Map<Path, Long> gotReadsByPath = samHandler.computeReadsByPaths();
    assertEquals(gotReadsByPath, expectedReadsByPath);
  }

  @Test(expected = SAMFormatException.class)
  public void samHandlerMustFailIfInvalidFileProvided() {
    String[] testArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test3.sam")};
    ParsedArguments parsedArguments = CommandLineParser.parse(testArgs);
    new SamHandler(parsedArguments.getSamPaths()).computeReadsByPaths();
  }
}
