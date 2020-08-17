import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.variant.variantcontext.VariantContext;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.TestHelper.callerTestFilePath;


public class CallerTest {

  @Test
  public void smallDataSpeedTest() throws IOException {
    callerAlgorithmSpeedTest(100, "short_seq.fasta", "short_seq.sam");
  }

  @Test
  public void bigDataSpeedTest() throws IOException {
    callerAlgorithmSpeedTest(100, "cv1.fasta", "cv1_grouped.sam");
  }

  public void callerAlgorithmSpeedTest(int iterations, String fastaFile, String samFile)
      throws IOException {
    TestReport testReport = new TestReport(fastaFile, samFile);
    List<Long> timeMeasurements = new ArrayList<>();
    for (int i = 0; i < iterations; i++) {
      Caller caller = getCaller(fastaFile, samFile);
      Instant start = Instant.now();
      caller.findVariants();
      Instant finish = Instant.now();
      timeMeasurements.add(Duration.between(start, finish).toMillis());
    }
    testReport.timeMeasurements = timeMeasurements;
    System.out.println(testReport);
  }

  private static Caller getCaller(String fastaFile, String samFile) {
    String[] arguments = getArgs(fastaFile, samFile);
    ParsedArguments parsedArguments = CommandLineParser.parse(arguments);
    IndexedFastaSequenceFile fastaSequenceFile =
        new FastaHandler(parsedArguments).getFastaSequenceFile();
    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
    return new Caller(fastaSequenceFile, samRecords);
  }

  private static String[] getArgs(String fastaFile, String samFile) {
    return new String[]{
        "--fasta", callerTestFilePath(fastaFile),
        "--sam", callerTestFilePath(samFile)
    };
  }

  private static class TestReport {
    public long totalReadsLength;
    public long referenceLength;
    public List<Long> timeMeasurements;

    public TestReport(String fastaFile, String samFile) {
      String[] arguments = getArgs(fastaFile, samFile);
      ParsedArguments parsedArguments = CommandLineParser.parse(arguments);
      IndexedFastaSequenceFile fastaSequenceFile =
          new FastaHandler(parsedArguments).getFastaSequenceFile();
      ReferenceSequence referenceSequence;
      while ((referenceSequence = fastaSequenceFile.nextSequence()) != null) {
        referenceLength += referenceSequence.length();
      }
      List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
      for (SAMRecord samRecord : samRecords) {
        totalReadsLength += samRecord.getReadLength();
      }
    }

    private double getAverageTime() {
      return timeMeasurements.stream().collect(Collectors.averagingLong(Long::longValue));
    }

    @Override
    public String toString() {
      return String.format(
          "test report:%ntotal reads length: %d%nreference length: %d%navg time: %f%n",
          totalReadsLength,
          referenceLength,
          getAverageTime()
      );
    }
  }
}
