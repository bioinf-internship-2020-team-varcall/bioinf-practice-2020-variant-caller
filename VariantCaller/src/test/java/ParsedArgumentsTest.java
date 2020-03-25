import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import org.junit.jupiter.api.Test;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ParsedArgumentsTest {

  @Test
  public void parsedArgumentsMustBeCreatedWithValidParameters() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test1.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    ParsedArguments parsedArguments = new ParsedArguments(correctFasta, correctBed, correctSam);

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
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(invalidFasta, correctBed, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfLessThanOneBedPathProvided() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> invalidBed = List.of();
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, invalidBed, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfLessThanOneSamPathProvided() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> invalidSam = List.of();
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, correctBed, invalidSam)
    );
  }

  @Test
  public void parsedArgumentsFailIfMoreThanOneFastaPathProvided() {
    List<Path> invalidFasta = List.of(
        Paths.get(testFilePath("test1.fasta")),
        Paths.get(testFilePath("test2.fasta"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(invalidFasta, correctBed, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustBeBuiltWithRemovedDuplicatedPaths() {
    List<Path> dupllicatedFasta = List.of(
        Paths.get(testFilePath("test1.fasta")),
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> duplicatedBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> duplicatedSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );

    Path expectedFastaPath = Paths.get(testFilePath("test1.fasta"));
    List<Path> expectedBedPaths = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> expectedSamPaths = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );

    ParsedArguments result = new ParsedArguments(dupllicatedFasta, duplicatedBed, duplicatedSam);

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
    List<Path> fastaWithInvalidExt = List.of(
        Paths.get(testFilePath("test1.fas"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(fastaWithInvalidExt, correctBed, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeBedPathHasInvalidExtension() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> bedWithInvalidExt = List.of(
        Paths.get(testFilePath("test1.bek")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, bedWithInvalidExt, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeSamPathHasInvalidExtension() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> samWithInvalidExt = List.of(
        Paths.get(testFilePath("test1.samuel")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, correctBed, samWithInvalidExt)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfFastaFileDoesNotExist() {
    List<Path> notExistingFasta = List.of(
        Paths.get(testFilePath("test217.fasta"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(notExistingFasta, correctBed, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeBedFileDoesNotExist() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> notExistingBed = List.of(
        Paths.get(testFilePath("test217.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> correctSam = List.of(
        Paths.get(testFilePath("test1.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, notExistingBed, correctSam)
    );
  }

  @Test
  public void parsedArgumentsMustFailIfSomeSamFileDoesNotExist() {
    List<Path> correctFasta = List.of(
        Paths.get(testFilePath("test1.fasta"))
    );
    List<Path> correctBed = List.of(
        Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed"))
    );
    List<Path> notExistingSam = List.of(
        Paths.get(testFilePath("test217.sam")),
        Paths.get(testFilePath("test2.sam"))
    );
    assertThrows(IllegalArgumentException.class, () ->
        new ParsedArguments(correctFasta, correctBed, notExistingSam)
    );
  }
}
