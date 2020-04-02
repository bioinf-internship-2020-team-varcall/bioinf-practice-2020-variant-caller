import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import org.junit.jupiter.api.Test;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ParsedArgumentsTest {

  @Test
  public void parsedArgumentsMustBeCreatedWithValidParameters() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    String correctRegion = "";
    ParsedArguments parsedArguments = new ParsedArguments(correctFasta, correctBed,
        correctSam, correctRegion);

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
  }

  @Test
  public void parsedArgumentsMustFailIfLessThanOneFastaPathProvided() {
    List<Path> invalidFasta = List.of();
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(invalidFasta, correctBed, correctSam, correctRegion)
    );
  }
//
//  @Test
//  public void parsedArgumentsMustFailIfLessThanOneBedPathProvided() {
//    List<Path> correctFasta = getPaths("test1.fasta");
//    List<Path> invalidBed = List.of();
//    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
//    String correctRegion = "";
//    assertThrows(IllegalArgumentException.class, () ->
//        new ParsedArguments(correctFasta, invalidBed, correctSam, correctRegion)
//    );
//  }

  @Test
  public void parsedArgumentsMustFailIfLessThanOneSamPathProvided() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> invalidSam = List.of();
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, correctBed, invalidSam, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsFailIfMoreThanOneFastaPathProvided() {
    List<Path> invalidFasta = getPaths("test1.fasta", "test2.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(invalidFasta, correctBed, correctSam, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsMustBeBuiltWithRemovedDuplicatedPaths() {
    List<Path> duplicatedFasta = getPaths("test1.fasta", "test1.fasta");
    List<Path> duplicatedBed = getPaths("test1.bed", "test1.bed", "test2.bed");
    List<Path> duplicatedSam = getPaths("test1.sam", "test1.sam", "test2.sam");
    String correctRegion = "";

    Path expectedFastaPath = Paths.get(testFilePath("test1.fasta"));
    List<Path> expectedBedPaths = getPaths("test1.bed", "test2.bed");
    List<Path> expectedSamPaths = getPaths("test1.sam", "test2.sam");

    ParsedArguments result = new ParsedArguments(duplicatedFasta, duplicatedBed,
        duplicatedSam, correctRegion);

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
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(fastaWithInvalidExt, correctBed, correctSam, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeBedPathHasInvalidExtension() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> bedWithInvalidExt = getPaths("test1.bek", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, bedWithInvalidExt, correctSam, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeSamPathHasInvalidExtension() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> samWithInvalidExt = getPaths("test1.samuel", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, correctBed, samWithInvalidExt, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfFastaFileDoesNotExist() {
    List<Path> notExistingFasta = getPaths("test217.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(notExistingFasta, correctBed, correctSam, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeBedFileDoesNotExist() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> notExistingBed = getPaths("test217.bed", "test2.bed");
    List<Path> correctSam = getPaths("test1.sam", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, notExistingBed, correctSam, correctRegion)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeSamFileDoesNotExist() {
    List<Path> correctFasta = getPaths("test1.fasta");
    List<Path> correctBed = getPaths("test1.bed", "test2.bed");
    List<Path> notExistingSam = getPaths("test217.sam", "test2.sam");
    String correctRegion = "";
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, correctBed, notExistingSam, correctRegion)
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
