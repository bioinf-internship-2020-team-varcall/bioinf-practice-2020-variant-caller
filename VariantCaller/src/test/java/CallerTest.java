import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.VariantContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static helpers.UnitTestHelper.callerRefFilePath;
import static helpers.UnitTestHelper.callerTestFilePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallerTest {

  @Test
  public void callerMustReturnCorrectListOfContexts() throws IOException {
    ParsedArguments parsedArguments = CommandLineParser.parse(getArgs());
    IndexedFastaSequenceFile fastaSequenceFile =
        new FastaHandler(parsedArguments).getFastaSequenceFile();
    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
    List<VariantContext> result = new Caller(fastaSequenceFile, samRecords).findVariants();
    assertEquals(Files.readString(callerRefFilePath("short_sequence_variants.txt")), result.toString());
  }

  private String[] getArgs() {
    return new String[]{
        "--fasta", callerTestFilePath("short_seq.fasta"),
        "--sam", callerTestFilePath("short_seq.sam")
    };
  }
}
