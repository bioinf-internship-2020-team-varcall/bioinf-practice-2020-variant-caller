import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.fasta.FastaHandlerUnableToFindEntryException;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests are temporary and will be changed in future versions.
 */
public class FastaHandlerTest {
  @ParameterizedTest
  @MethodSource("provideArgumentsForMustReturnCorrectSubsequence")
  public void fastaHandlerMustReturnCorrectSubsequence(
      String fileName, String expectedBaseString, String contig, long start, long stop) {
    FastaHandler fastaHandler = getFastaHandler(fileName);
    ReferenceSequence referenceSequence = fastaHandler.getSubsequence(contig, start, stop);
    assertEquals(expectedBaseString, referenceSequence.getBaseString());
  }

  @ParameterizedTest
  @MethodSource("provideArgumentsForMustFailGettingIncorrectSubsequence")
  public void fastaHandlerFailGettingIncorrectSubsequence(String contig, long start, long stop) {
    FastaHandler fastaHandler = getFastaHandler("test1.fasta");
    assertThrows(FastaHandlerUnableToFindEntryException.class,
        () -> fastaHandler.getSubsequence(contig, start, stop));
  }

  @Test
  public void fastaHandlerMustReturnIndexedFile() {
    FastaHandler fastaHandler = getFastaHandler("test1.fasta");
    IndexedFastaSequenceFile sequenceFile = fastaHandler.getFastaSequenceFile();
    assertTrue(sequenceFile.isIndexed());
  }

  private static Stream<Arguments> provideArgumentsForMustReturnCorrectSubsequence() {
    return Stream.of(
        Arguments.of("test1.fasta", "CCCCTA", "TEST", 10, 15),
        Arguments.of("test3.fasta", "CCCCTA", "chr1", 10, 15),
        Arguments.of("test1.fna", "AGCTTT", "NC_000913.3", 1, 6)
    );
  }

  private static Stream<Arguments> provideArgumentsForMustFailGettingIncorrectSubsequence() {
    return Stream.of(
        Arguments.of("TEST1", 10, 15),
        Arguments.of("TEST", 10, 1500)
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
