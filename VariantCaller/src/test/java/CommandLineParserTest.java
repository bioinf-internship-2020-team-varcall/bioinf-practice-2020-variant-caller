import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import static com.epam.bioinf.variantcaller.cmdline.CommandLineMessages.*;
import joptsimple.OptionSet;
import org.junit.jupiter.api.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import joptsimple.OptionException;

import static org.junit.rules.ExpectedException.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineParserTest {
  private final String currentPathString = Paths.get("src/test/resources").toAbsolutePath().toString();

  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void parserMustAcceptValidParameters() {
    try {
      String[] correctTestArgs = {
          "--fasta", currentPathString + "/test1.fasta",
          "--bed", currentPathString + "/test1.bed",
          "--sam", currentPathString + "/test1.sam"
      };
      CommandLineParser result = CommandLineParser.parse(correctTestArgs);
      OptionSet parserOptions = result.getOptions();

      assertTrue(parserOptions.has("fasta"), "Parser must accept --fasta parameter");
      assertTrue(parserOptions.has("bed"), "Parser must accept --bed parameter");
      assertTrue(parserOptions.has("sam"), "Parser must accept --sam parameter");
    } catch (Exception e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void parserMustFailIfInvalidParameters() {
    String[] invalidTestArgs = {
        "-p", currentPathString + "/test1.fasta",
        "-ap", currentPathString + "/test1.bed",
        "-hp", currentPathString + "/test1.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustAcceptMultipleArguments() {
    String[] correctTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser result = CommandLineParser.parse(correctTestArgs);
      String expectedFastaPath = currentPathString + "/test1.fasta";
      String[] expectedBedPaths = {currentPathString + "/test1.bed", currentPathString + "/test2.bed"};
      String[] expectedSamPaths = {currentPathString + "/test1.sam", currentPathString + "/test2.sam"};
      Arrays.sort(expectedBedPaths);
      Arrays.sort(expectedSamPaths);

      String parsedFastaPath = result.getFastaPath().toString();
      List<String> parsedBedPaths = result.getBedPaths().stream().map(Path::toString).sorted().collect(Collectors.toList());
      List<String> parsedSamPaths = result.getSamPaths().stream().map(Path::toString).sorted().collect(Collectors.toList());

      assertEquals(expectedFastaPath, parsedFastaPath, "Expected fasta path is not equal to parsed one");
      for (int i = 0; i < 2; i++) {
        assertEquals(expectedBedPaths[i], parsedBedPaths.get(i), "Expected bed path is not equal to parsed one");
        assertEquals(expectedSamPaths[i], parsedSamPaths.get(i), "Expected sam path is not equal to parsed one");
      }
    } catch (Exception e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void parserMustFailIfMoreThanOneFastaPathProvided() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta" + ":" + currentPathString + "/test2.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), FASTA_ARGS_COUNT_EXC, "Exception was thrown but its message is not equal to 'FASTA_ARGS_COUNT_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfLessThanOneFastaPathProvided() {
    String[] invalidTestArgs = {
        "--fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfLessThanOneBedPathProvided() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfLessThanOneSamPathProvided() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustRemoveDuplicatedPaths() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta" + ":" + currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser result = CommandLineParser.parse(invalidTestArgs);

      String expectedFastaPath = currentPathString + "/test1.fasta";
      String[] expectedBedPaths = {currentPathString + "/test1.bed", currentPathString + "/test2.bed"};
      String[] expectedSamPaths = {currentPathString + "/test1.sam", currentPathString + "/test2.sam"};

      assertNotNull(result.getFastaPath(), "Parser returned null instead of fasta path");
      assertEquals(result.getBedPaths().size(), 2, "Amount of parsed bed paths is not equal to expected");
      assertEquals(result.getSamPaths().size(), 2, "Amount of parsed sam paths is not equal to expected");

      String parsedFastaPath = result.getFastaPath().toString();
      List<String> parsedBedPaths = result.getBedPaths().stream().map(Path::toString).sorted().collect(Collectors.toList());
      List<String> parsedSamPaths = result.getSamPaths().stream().map(Path::toString).sorted().collect(Collectors.toList());

      assertEquals(expectedFastaPath, parsedFastaPath, "Expected fasta path is not equal to parsed");
      for (int i = 0; i < 2; i++) {
        assertEquals(expectedBedPaths[i], parsedBedPaths.get(i), "Expected bed path is not equal to parsed");
        assertEquals(expectedSamPaths[i], parsedSamPaths.get(i), "Expected sam path is not equal to parsed");
      }
    } catch (Exception e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void parserMustFailIfFastaPathHasInvalidExtension() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fas",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), FASTA_EXTENSION_EXC, "Exception was thrown but its message is not equal to 'FASTA_EXTENSION_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfSomeBedPathHasInvalidExtension() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.sam",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), BED_EXTENSION_EXC, "Exception was thrown but its message is not equal to 'BED_EXTENSION_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfSomeSamPathHasInvalidExtension() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.fasta"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), SAM_EXTENSION_EXC, "Exception was thrown but its message is not equal to 'SAM_EXTENSION_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfFastaFileDoesNotExist() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/testOne.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), FASTA_PATH_NOT_EXISTS_EXC, "Exception was thrown but its message is not equal to 'FASTA_PATH_NOT_EXISTS_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfSomeBedFileDoesNotExist() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/testTwo.bed",
        "--sam", currentPathString + "/test1.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), BED_PATH_NOT_EXISTS_EXC, "Exception was thrown but its message is not equal to 'BED_PATH_NOT_EXISTS_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }

  @Test
  public void parserMustFailIfSomeSamFileDoesNotExist() {
    String[] invalidTestArgs = {
        "--fasta", currentPathString + "/test1.fasta",
        "--bed", currentPathString + "/test1.bed" + ":" + currentPathString + "/test2.bed",
        "--sam", currentPathString + "/testOne.sam" + ":" + currentPathString + "/test2.sam"
    };
    try {
      CommandLineParser.parse(invalidTestArgs);
    } catch (Exception e) {
      assertEquals(e.getLocalizedMessage(), SAM_PATH_NOT_EXISTS_EXC, "Exception was thrown but its message is not equal to 'SAM_PATH_NOT_EXISTS_EXC' constant");
      thrown.expect(OptionException.class);
    }
  }
}
