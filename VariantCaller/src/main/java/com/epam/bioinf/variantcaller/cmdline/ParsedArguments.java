package com.epam.bioinf.variantcaller.cmdline;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.bioinf.variantcaller.cmdline.CommandLineParser.CommandLineMessages.*;
import static com.epam.bioinf.variantcaller.cmdline.ParsedArguments.AllowedExtensions.*;

/**
 * Class holds validated data
 */
public class ParsedArguments {
  private final Path fastaPath;
  private final List<Path> bedPaths;
  private final List<Path> samPaths;

  /**
   * Costructor validates and holds parsed arguments
   */
  public ParsedArguments(List<Path> fastaPaths, List<Path> bedPaths, List<Path> samPaths) {
    List<Path> processedFasta = withRemovedDuplicates(fastaPaths);
    List<Path> processedBed = withRemovedDuplicates(bedPaths);
    List<Path> processedSam = withRemovedDuplicates(samPaths);

    validate(processedFasta, processedBed, processedSam);

    this.fastaPath = processedFasta.get(0);
    this.bedPaths = processedBed;
    this.samPaths = processedSam;
  }

  private void validate(List<Path> fastaValues, List<Path> bedValues, List<Path> samValues) {
    String errorMessage = "";
    if (fastaValues.size() != 1) errorMessage = FASTA_ARGS_COUNT_EXC;
    else if (bedValues.size() == 0) errorMessage = BED_ARGS_COUNT_EXC;
    else if (samValues.size() == 0) errorMessage = SAM_ARGS_COUNT_EXC;

    else if (checkIfSomeExtensionsIsInvalid(fastaValues, FASTA_EXTENSIONS))
      errorMessage = FASTA_EXTENSION_EXC;
    else if (checkIfSomeExtensionsIsInvalid(bedValues, BED_EXTENSIONS))
      errorMessage = BED_EXTENSION_EXC;
    else if (checkIfSomeExtensionsIsInvalid(samValues, SAM_EXTENSIONS))
      errorMessage = SAM_EXTENSION_EXC;

    else if (checkIfSomePathDoesNotExist(fastaValues)) errorMessage = FASTA_PATH_NOT_EXISTS_EXC;
    else if (checkIfSomePathDoesNotExist(bedValues)) errorMessage = BED_PATH_NOT_EXISTS_EXC;
    else if (checkIfSomePathDoesNotExist(samValues)) errorMessage = SAM_PATH_NOT_EXISTS_EXC;

    if (!errorMessage.isEmpty()) throw new IllegalArgumentException(errorMessage);
  }

  private boolean checkIfSomePathDoesNotExist(List<Path> paths) {
    return paths.stream().anyMatch(path -> !Files.exists(path));
  }

  private boolean checkIfSomeExtensionsIsInvalid(List<Path> paths, String... allowedExts) {
    if (paths.stream().allMatch(path -> path.toString().contains("."))) {
      return paths
          .stream()
          .map(path -> path.toString().substring(path.toString().lastIndexOf(".") + 1))
          .anyMatch(ext -> !Arrays.asList(allowedExts).contains(ext));
    }
    return true;
  }

  private List<Path> withRemovedDuplicates(List<Path> rawList) {
    return rawList.stream().distinct().collect(Collectors.toList());
  }

  public Path getFastaPath() {
    return fastaPath;
  }

  public List<Path> getBedPaths() {
    return Collections.unmodifiableList(bedPaths);
  }

  public List<Path> getSamPaths() {
    return Collections.unmodifiableList(samPaths);
  }

  public static final class AllowedExtensions {
    private AllowedExtensions() {
      // restrict instantiation
    }

    public static final String[] FASTA_EXTENSIONS = {"fasta", "fna", "fa"};
    public static final String[] BED_EXTENSIONS = {"bed"};
    public static final String[] SAM_EXTENSIONS = {"sam"};
  }
}
