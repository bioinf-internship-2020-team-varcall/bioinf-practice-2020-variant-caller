import com.epam.bioinf.variantcaller.ParsedData;
import static com.epam.bioinf.variantcaller.cmdline.CommandLineMessages.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.rules.ExpectedException.none;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandLineArgsrIntegTest {
  private final String currentPathString = Paths.get("src/integrationTest/resources").toAbsolutePath().toString();

  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void programMustWorkWithCorrectArguments() {
    String[] correctTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      ParsedData parsedData = ParsedData.createParsedDataFrom(correctTestArgs);
      assertNotNull(parsedData, "Parsed data must not return null if job failed");
      Path[] expectedBedFiles = { Paths.get(currentPathString + "/test1.bed"), Paths.get(currentPathString + "/test2.bed") };
      Path[] expectedSamFiles = { Paths.get(currentPathString + "/test1.sam"), Paths.get(currentPathString + "/test2.sam") };
      Arrays.sort(expectedBedFiles);
      Arrays.sort(expectedSamFiles);
      List<Path> gotBedFiles = parsedData.getResultBed().stream().sorted().collect(Collectors.toList());
      List<Path> gotSamFiles = parsedData.getResultSam().stream().sorted().collect(Collectors.toList());
      assertEquals(parsedData.getResultFasta(), Paths.get(currentPathString + "/test1.fasta"), "Expected fasta path must be equal to parsed by program");
      for (int i = 0; i < 2; i++) {
        assertEquals(expectedBedFiles[i], gotBedFiles.get(i), "Expected bed files must be equal to parsed by program");
        assertEquals(expectedSamFiles[i], gotSamFiles.get(i), "Expected sam files must be equal to parsed by program");
      }
    } catch (Exception ex) {
      fail(ex.getLocalizedMessage());
    }
  }

  @Test
  public void programMustFailWithInvalidArguments() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta" + ":" + currentPathString + "/test2.bed",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      ParsedData.createParsedDataFrom(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), FASTA_ARGS_COUNT_EXC, "Exception was thrown but its message is not equal to 'FASTA_ARGS_COUNT_EXC' constant");
    }
  }
}
