import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaArgsSizeException;
import com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaInvalidExtensionException;
import com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaPathNotExistsException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionInvalidException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionInvalidExtensionException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionPathNotExistsException;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamArgsSizeException;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamInvalidExtensionException;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamPathNotExistsException;
import org.junit.jupiter.api.Test;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.TEST_RESOURCES_ROOT;
import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParsedArgumentsTest {
  // Used to imitate correct work of ParsedArguments
  private static final Optional<String> CORRECT_REGION = Optional.of("chr1 1 10");
  private static final Optional<Path> outputDir = Optional.of(TEST_RESOURCES_ROOT);


  @Test
  public void parsedArgumentsMustBeCreatedWithValidParameters() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    Optional<String> expectedRegion = Optional.of("chr1 1 10");
    ParsedArguments parsedArguments = new ParsedArguments(correctFasta, correctBed,
        correctSam, CORRECT_REGION, outputDir);

    assertEquals(correctFasta.get(0), parsedArguments.getFastaPath());
    assertEquals(
        Set.copyOf(correctBed),
        Set.copyOf(parsedArguments.getBedPaths()),
        "Unexpected BED file paths"
    );
    assertEquals(
        Set.copyOf(correctSam),
        Set.copyOf(parsedArguments.getSamPaths()),
        "Unexpected SAM file paths"
    );
    assertEquals(expectedRegion, parsedArguments.getRegionData());
  }

  @Test
  public void parsedArgumentsMustFailIfLessThanOneFastaPathProvided() {
    List<Path> invalidFasta = List.of();
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    assertThrows(FastaArgsSizeException.class, () ->
        new ParsedArguments(invalidFasta, correctBed, correctSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfRegionSpecifiedIncorrectly() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = List.of();
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    Optional<String> incorrectRegion = Optional.of("chr1 chr1 12 15");
    assertThrows(RegionInvalidException.class, () ->
        new ParsedArguments(correctFasta, correctBed, correctSam, incorrectRegion, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfLessThanOneSamPathProvided() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> invalidSam = List.of();
    assertThrows(SamArgsSizeException.class, () ->
        new ParsedArguments(correctFasta, correctBed, invalidSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsFailIfMoreThanOneFastaPathProvided() {
    List<Path> invalidFasta = getPaths("test1.fasta", "test2.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    assertThrows(FastaArgsSizeException.class, () ->
        new ParsedArguments(invalidFasta, correctBed, correctSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustBeBuiltWithRemovedDuplicatedPaths() {
    List<Path> duplicatedFasta = getPaths("test1.fasta", "test1.fasta");
    List<Path> duplicatedBed = getPaths("test1.bed", "test1.bed", "test2.bed");
    List<Path> duplicatedSam = getPaths("test1.sam", "test1.sam", "test2.sam");

    Path expectedFastaPath = Paths.get(testFilePath("test1.fasta"));
    List<Path> expectedBedPaths = getPaths("test1.bed", "test2.bed");
    List<Path> expectedSamPaths = getPaths("test1.sam", "test2.sam");

    ParsedArguments result = new ParsedArguments(duplicatedFasta, duplicatedBed,
        duplicatedSam, CORRECT_REGION, outputDir);

    Path parsedFastaPath = result.getFastaPath();
    List<Path> parsedBedPaths = result.getBedPaths();
    List<Path> parsedSamPaths = result.getSamPaths();

    assertNotNull(parsedFastaPath, "Parser returned null instead of fasta path");
    assertEquals(2, parsedBedPaths.size(), "Amount of parsed bed paths is not equal to expected");
    assertEquals(2, parsedSamPaths.size(), "Amount of parsed sam paths is not equal to expected");

    assertEquals(expectedFastaPath, parsedFastaPath, "Expected fasta path is not equal to parsed");
    assertEquals(
        Set.copyOf(expectedBedPaths),
        Set.copyOf(parsedBedPaths),
        "Unexpected BED file paths"
    );
    assertEquals(
        Set.copyOf(expectedSamPaths),
        Set.copyOf(parsedSamPaths),
        "Unexpected SAM file paths"
    );
  }

  @Test
  public void parsedArgumentsMustFailIfFastaPathHasInvalidExtension() {
    List<Path> fastaWithInvalidExt = getPaths("test1.fas");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    assertThrows(FastaInvalidExtensionException.class, () ->
        new ParsedArguments(fastaWithInvalidExt, correctBed, correctSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeBedPathHasInvalidExtension() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> bedWithInvalidExt = getPaths("test1.bek", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    assertThrows(RegionInvalidExtensionException.class, () ->
        new ParsedArguments(correctFasta, bedWithInvalidExt, correctSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeSamPathHasInvalidExtension() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> samWithInvalidExt = getPaths("test1.samuel", "test2.sam");
    assertThrows(SamInvalidExtensionException.class, () ->
        new ParsedArguments(correctFasta, correctBed, samWithInvalidExt, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfFastaFileDoesNotExist() {
    List<Path> notExistingFasta = getPaths("test217.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    assertThrows(FastaPathNotExistsException.class, () ->
        new ParsedArguments(notExistingFasta, correctBed, correctSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeBedFileDoesNotExist() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> notExistingBed = getPaths("test217.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    assertThrows(RegionPathNotExistsException.class, () ->
        new ParsedArguments(correctFasta, notExistingBed, correctSam, CORRECT_REGION, outputDir)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeSamFileDoesNotExist() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> notExistingSam = getPaths("test217.sam", "test2.sam");
    assertThrows(SamPathNotExistsException.class, () ->
        new ParsedArguments(correctFasta, correctBed, notExistingSam, CORRECT_REGION, outputDir)
    );
  }

  private List<Path> getPaths(String... filenames) {
    List<Path> paths = new ArrayList<>();
    Arrays.asList(filenames).forEach(filename ->
        paths.add(Paths.get(testFilePath(filename)))
    );
    return paths;
  }
}
