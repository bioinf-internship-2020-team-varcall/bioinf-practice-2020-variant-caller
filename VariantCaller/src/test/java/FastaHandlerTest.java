import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHadler;
import htsjdk.samtools.SAMException;
import org.junit.jupiter.api.Test;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static org.junit.jupiter.api.Assertions.*;

public class FastaHandlerTest {

  @Test
  public void fastaHandlerMustReturnCorrectGcContentWithMockExample() {
    FastaHadler fastaHadler = getFastaHadler("test1.fasta");
    double expectedGcContent = 69.52;
    double gotGcContent = fastaHadler.getGcContent();
    assertEquals(gotGcContent, expectedGcContent, 0.01);
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithMockExample() {
    FastaHadler fastaHadler = getFastaHadler("test1.fasta");
    int expectedNucleotidesCount = 420;
    assertEquals(fastaHadler.countNucleotides(), expectedNucleotidesCount);
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithRealExample() {
    FastaHadler fastaHadler = getFastaHadler("test1.fna");
    int expectedNucleotidesCount = 4641652;
    assertEquals(fastaHadler.countNucleotides(), expectedNucleotidesCount);
  }

  @Test
  public void fastaHandlerMustReturnCorrectGcContentWithRealExample() {
    FastaHadler fastaHadler = getFastaHadler("test1.fna");
    double expectedGcContent = 50.8;
    double gotGcContent = fastaHadler.getGcContent();
    assertEquals(gotGcContent, expectedGcContent, 0.01);
  }

  @Test
  public void fastaHandlerMustFailIfNoSequenceWasProvided() {
    assertThrows(SAMException.class, () ->
        getFastaHadler("test2.fasta")
    );
  }

  @Test
  public void fastaHandlerMustFailIfMultipleSequencesWereProvided() {
    assertThrows(IllegalArgumentException.class, () ->
        getFastaHadler("test3.fasta")
    );
  }



  private FastaHadler getFastaHadler(String s) {
    String[] correctTestArgs = getArgs(s);
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    return new FastaHadler(parsedArguments.getFastaPath());
  }

  private String[] getArgs(String s) {
    return new String[]{
        "--fasta", testFilePath(s),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
  }
}
