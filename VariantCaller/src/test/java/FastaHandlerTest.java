import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import htsjdk.samtools.SAMException;
import org.junit.jupiter.api.Test;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests are temporary and will be changed in future versions.
 */
public class FastaHandlerTest {

  @Test
  public void fastaHandlerMustReturnCorrectGcContentWithMockExample() {
    FastaHandler fastaHandler = getFastaHandler("test1.fasta");
    double expectedGcContent = 69.52;
    double actualGcContent = fastaHandler.getGcContent();
    assertEquals(expectedGcContent, actualGcContent, 0.01);
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithMockExample() {
    FastaHandler fastaHandler = getFastaHandler("test1.fasta");
    int expectedNucleotidesCount = 420;
    assertEquals(expectedNucleotidesCount, fastaHandler.countNucleotides());
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithRealExample() {
    FastaHandler fastaHandler = getFastaHandler("test1.fna");
    int expectedNucleotidesCount = 4641652;
    assertEquals(expectedNucleotidesCount, fastaHandler.countNucleotides());
  }

  @Test
  public void fastaHandlerMustReturnCorrectGcContentWithRealExample() {
    FastaHandler fastaHandler = getFastaHandler("test1.fna");
    double expectedGcContent = 50.8;
    double actualGcContent = fastaHandler.getGcContent();
    assertEquals(expectedGcContent, actualGcContent, 0.01);
  }

  @Test
  public void fastaHandlerMustFailIfNoSequenceWasProvided() {
    assertThrows(SAMException.class, () ->
        getFastaHandler("test2.fasta")
    );
  }

  @Test
  public void fastaHandlerMustFailIfMultipleSequencesWereProvided() {
    assertThrows(IllegalArgumentException.class, () ->
        getFastaHandler("test3.fasta")
    );
  }

  private FastaHandler getFastaHandler(String fastaFileName) {
    String[] correctTestArgs = getArgs(fastaFileName);
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    return new FastaHandler(parsedArguments);
  }

  private String[] getArgs(String fastaFileName) {
    return new String[]{
        "--fasta", testFilePath(fastaFileName),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
  }
}
