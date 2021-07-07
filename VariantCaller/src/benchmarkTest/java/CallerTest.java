import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static helpers.TestHelper.callerTestFilePath;


public class CallerTest {

  private static final PrintStream OUTPUT_STREAM = System.err;

  @Test
  public void smallDataSpeedTest() throws IOException {
    TestReport testReport = new TestReport("short_seq.fasta", "short_seq.sam");
    testReport.setAverageExecutionTime(
        callerAlgorithmSpeedTest(100, "short_seq.fasta", "short_seq.sam"));
    testReport.setMemoryConsumption(
        callerAlgorithmMemoryConsumptionTest("short_seq.fasta", "short_seq.sam"));
    OUTPUT_STREAM.println(testReport);
  }

  @Test
  public void bigDataSpeedTest() throws IOException {
    TestReport testReport = new TestReport("cv1.fasta", "cv1_grouped.sam");
    testReport.setAverageExecutionTime(
        callerAlgorithmSpeedTest(100, "cv1.fasta", "cv1_grouped.sam"));
    testReport.setMemoryConsumption(
        callerAlgorithmMemoryConsumptionTest("cv1.fasta", "cv1_grouped.sam"));
    OUTPUT_STREAM.println(testReport);
  }

//  @Test
//  public void realDataSpeedTest() throws IOException {
//    callerAlgorithmSpeedTest(1, "ecoli.fasta", "new_ecoli.sam");
//  }

  private List<Long> callerAlgorithmSpeedTest(int iterations, String fastaFile, String samFile)
      throws IOException {
    List<Long> timeMeasurements = new ArrayList<>();
    for (int i = 0; i < iterations; i++) {
      Caller caller = getCaller(fastaFile, samFile);
      Instant start = Instant.now();
      caller.findVariants();
      Instant finish = Instant.now();
      timeMeasurements.add(Duration.between(start, finish).toMillis());
    }
    return timeMeasurements;
  }

  @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE", "DM_GC"})
  private long callerAlgorithmMemoryConsumptionTest(String fastaFile, String samFile) {
    Runtime runtime = Runtime.getRuntime();
    Caller caller = getCaller(fastaFile, samFile);
    System.gc();
    long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
    var holderArray = caller.findVariants();
    System.gc();
    long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
    return memoryAfter - memoryBefore;
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
    return new String[] {
        "--fasta", callerTestFilePath(fastaFile),
        "--sam", callerTestFilePath(samFile)
    };
  }

  private static class TestReport {
    private long totalReadsLength;
    private long referenceLength;
    private double averageExecutionTime;
    private long memoryConsumption;

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

    private void setAverageExecutionTime(List<Long> timeMeasurements) {
      this.averageExecutionTime = timeMeasurements.stream()
          .collect(Collectors.averagingLong(Long::longValue));
    }

    private void setMemoryConsumption(long memoryConsumption) {
      this.memoryConsumption = memoryConsumption;
    }

    @Override
    public String toString() {
      return String.format(
          "test report:%ntotal reads length: %d%nreference length: %d%navg time: %f%n" +
              "memory consumption: %d%n",
          totalReadsLength,
          referenceLength,
          averageExecutionTime,
          memoryConsumption
      );
    }
  }
}
