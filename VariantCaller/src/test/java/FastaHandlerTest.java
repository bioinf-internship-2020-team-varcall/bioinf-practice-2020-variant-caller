import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHadler;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.epam.bioinf.variantcaller.handlers.FastaHadler.FastaHadlerMessages.MULTIPLE_SEQUENCES_EXC;
import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

public class FastaHandlerTest {
  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void fastaHandlerMustReturnCorrectGcContentWithMockExample() {
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = new FastaHadler(parsedArguments.getFastaPath());
    double expectedGcContent = 69.5;
    double gotGcContent = BigDecimal.valueOf(fastaHadler.getGcContent())
        .setScale(1, RoundingMode.HALF_UP).doubleValue();
    assertEquals(gotGcContent, expectedGcContent);
  }

  @Test
  public void fastaHandlerMustReturnCorrectCountOfNucleotidesWithMockExample() {
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fasta"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = new FastaHadler(parsedArguments.getFastaPath());
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
    FastaHadler fastaHadler = new FastaHadler(parsedArguments.getFastaPath());
    int expectedNucleotidesCount = 4641652;
    assertEquals(fastaHadler.countNucleotides(), expectedNucleotidesCount);
  }

  @Test
  public void fastaHandlerMustReturnCorrectNucleotidesCountWithRealExample() {
    String[] correctTestArgs = {
        "--fasta", testFilePath("test1.fna"),
        "--bed", testFilePath("test1.bed"),
        "--sam", testFilePath("test1.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    FastaHadler fastaHadler = new FastaHadler(parsedArguments.getFastaPath());
    double expectedGcContent = 50.8;
    double gotGcContent = BigDecimal.valueOf(fastaHadler.getGcContent())
        .setScale(1, RoundingMode.HALF_UP).doubleValue();
    assertEquals(gotGcContent, expectedGcContent);
  }

  @Test
  public void fastaHandlerMustFailIfNoSequenceWasProvided() {
    try {
      String[] invalidTestArgs = {
          "--fasta", testFilePath("test2.fasta"),
          "--bed", testFilePath("test1.bed"),
          "--sam", testFilePath("test1.sam")
      };
      ParsedArguments parsedArguments = CommandLineParser.parse(invalidTestArgs);
      new FastaHadler(parsedArguments.getFastaPath());
      fail();
    } catch (Exception e) {
      thrown.expect(Exception.class);
    }
  }

  @Test
  public void fastaHandlerMustFailIfMultipleSequencesWereProvided() {
    try {
      String[] invalidTestArgs = {
          "--fasta", testFilePath("test3.fasta"),
          "--bed", testFilePath("test1.bed"),
          "--sam", testFilePath("test1.sam")
      };
      ParsedArguments parsedArguments = CommandLineParser.parse(invalidTestArgs);
      new FastaHadler(parsedArguments.getFastaPath());
      fail();
    } catch (Exception e) {
      thrown.expect(Exception.class);
      assertEquals(MULTIPLE_SEQUENCES_EXC, e.getMessage());
    }
  }
}
