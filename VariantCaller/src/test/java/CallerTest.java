import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.caller.Variant;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static helpers.UnitTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CallerTest {
  @Test
  public void callerMustReturnCorrectVariantsWithMockFastaAndSam() {
    String[] correctTestArgs = {
        "--fasta", callerTestFilePath("short_seq.fasta"),
        "--sam", callerTestFilePath("short_seq.sam")
    };

    try {
      File tempWarningOutput = File.createTempFile("test", ".temp");
      System.setOut(new PrintStream(tempWarningOutput, Charset.defaultCharset()));
      ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
      IndexedFastaSequenceFile fastaSequenceFile =
          new FastaHandler(parsedArguments).getFastaSequenceFile();
      List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
      new Caller(fastaSequenceFile, samRecords)
          .findVariants()
          .forEach(variant -> System.out.println(variant.toString()));
      List<String> linesRef = Files.readAllLines(callerRefFilePath("short_sequence_variants.txt"));
      List<String> linesProduced = Files.readAllLines(
              Paths.get(tempWarningOutput.getAbsolutePath()));
      linesProduced.removeIf(s -> !s.startsWith("Variant"));
      assertEquals(
          linesRef,
          linesProduced
      );
      tempWarningOutput.deleteOnExit();
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void callerMustReturnAnEmptyListIfNoVariantsWereFound() {
    String[] correctTestArgs = {
        "--fasta", callerTestFilePath("no_variants.fasta"),
        "--sam", callerTestFilePath("no_variants.sam")
    };
    ParsedArguments parsedArguments = CommandLineParser.parse(correctTestArgs);
    IndexedFastaSequenceFile fastaSequenceFile =
        new FastaHandler(parsedArguments).getFastaSequenceFile();
    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
    List<Variant> variants = new Caller(fastaSequenceFile, samRecords).findVariants();
    assertEquals(0, variants.size());
  }
}
