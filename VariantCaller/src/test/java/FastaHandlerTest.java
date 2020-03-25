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
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = getFastaHadler(parsedArguments);
    double expectedGcContent = 69.52;
    double gotGcContent = fastaHadler.getGcContent();
    assertEquals(gotGcContent, expectedGcContent, 0.01);
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithMockExample() {
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = getFastaHadler(parsedArguments);
    int expectedNucleotidesCount = 420;
    assertEquals(fastaHadler.countNucleotides(), expectedNucleotidesCount);
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithRealExample() {
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fna"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = getFastaHadler(parsedArguments);
    int expectedNucleotidesCount = 4641652;
    assertEquals(fastaHadler.countNucleotides(), expectedNucleotidesCount);
  }

  @Test
  public void fastaHandlerMustReturnCorrectGcContentWithRealExample() {
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fna"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = getFastaHadler(parsedArguments);
    double expectedGcContent = 50.8;
    double gotGcContent = fastaHadler.getGcContent();
    assertEquals(gotGcContent, expectedGcContent, 0.01);
  }

  @Test
  public void fastaHandlerMustFailIfNoSequenceWasProvided() {
    String[] invalidTestArgs = {
        "--fasta", testFilePath("test2.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(invalidTestArgs);
    assertThrows(SAMException.class, () ->
        new FastaHadler(parsedArguments.getFastaPath())
    );
  }

  @Test
  public void fastaHandlerMustFailIfMultipleSequencesWereProvided() {
    String[] invalidTestArgs = {
        "--fasta", testFilePath("test3.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(invalidTestArgs);
    assertThrows(IllegalArgumentException.class, () ->
        new FastaHadler(parsedArguments.getFastaPath())
    );
  }


  private FastaHadler getFastaHadler(ParsedArguments parsedArguments) {
    return new FastaHadler(parsedArguments.getFastaPath());
  }

}
