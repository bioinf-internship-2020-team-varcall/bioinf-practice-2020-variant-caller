import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
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
    callerAlgorithmSpeedTest(1000, "short_seq.fasta", "short_seq.sam");
  }

  @Test
  public void bigDataSpeedTest() throws IOException {
    callerAlgorithmSpeedTest(100, "cv1.fasta", "cv1_grouped.sam");
  }

  public void callerAlgorithmSpeedTest(int iterations, String fasta, String sam) throws IOException {
    TestReport testReport = new TestReport(fasta, sam);
    List<Long> timeMeasurements = new ArrayList<>();
    for (int i = 0; i < iterations; i++) {
      Caller caller = getCaller(fasta, sam);
      Instant start = Instant.now();
      caller.findVariants();
      Instant finish = Instant.now();
      timeMeasurements.add(Duration.between(start, finish).toMillis());
    }
    testReport.timeMeasurements = timeMeasurements;
    System.out.println(testReport.getAverageTime());
    System.out.println(testReport.referenceLength);
    System.out.println(testReport.totalReadsLength);
  }

  private static Caller getCaller(String fasta, String sam) {
    String[] arguments  = getArgs(fasta, sam);
    ParsedArguments parsedArguments = CommandLineParser.parse(arguments);
    IndexedFastaSequenceFile fastaSequenceFile =
        new FastaHandler(parsedArguments).getFastaSequenceFile();
    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
    return new Caller(fastaSequenceFile, samRecords);
  }

  private static String[] getArgs(String fasta, String sam) {
    return new String[]{
        "--fasta", callerTestFilePath(fasta),
        "--sam", callerTestFilePath(sam)
    };
  }

  private static class TestReport {
    public long totalReadsLength;
    public long referenceLength;
    public List<Long> timeMeasurements;
    public List<Long> memoryMeasurements;

    public TestReport(String fasta, String sam) {
      String[] arguments  = getArgs(fasta, sam);
      ParsedArguments parsedArguments = CommandLineParser.parse(arguments);
      IndexedFastaSequenceFile fastaSequenceFile =
          new FastaHandler(parsedArguments).getFastaSequenceFile();
      ReferenceSequence referenceSequence;
      while((referenceSequence = fastaSequenceFile.nextSequence()) != null) {
        referenceLength += referenceSequence.length();
      }
      List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
      for(SAMRecord samRecord : samRecords) {
        totalReadsLength += samRecord.getReadLength();
      }
    }

    public double getAverageTime() {
      return timeMeasurements.stream().collect(Collectors.averagingLong(Long::longValue));
    }
  }
}
