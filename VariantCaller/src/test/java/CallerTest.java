import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CallerTest {
//  @Test
//  public void testKek() {
//    String[] args = {"--fasta",
//        "/home/iamnzrv/VC/github/bioinformatics/fork/bioinf-practice-2020-variant-caller/VariantCaller/src/test/resources/ecoli.fasta",
//        "--sam",
//        "/home/iamnzrv/VC/github/bioinformatics/fork/bioinf-practice-2020-variant-caller/VariantCaller/src/test/resources/ecoli.sam"};
//    ParsedArguments parsedArguments = CommandLineParser.parse(args);
//    IndexedFastaSequenceFile fastaSequenceFile =
//        new FastaHandler(parsedArguments).getFastaSequenceFile();
//    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
//    new Caller().call(fastaSequenceFile, samRecords);
//  }
}
