package com.epam.bioinf.variantcaller.cmdline;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.bioinf.variantcaller.cmdline.CommandLineMessages.*;

//this class holds validated data
public class ParsedArguments {
  List<Path> fastaPaths;
  List<Path> bedPaths;
  List<Path> samPaths;

  public ParsedArguments(List<Path> fastaPaths, List<Path> bedPaths, List<Path> samPaths) {
    List<Path> processedFasta = withRemovedDuplicates(fastaPaths);
    List<Path> processedBed = withRemovedDuplicates(bedPaths);
    List<Path> processedSam = withRemovedDuplicates(samPaths);

    validate(processedFasta, processedBed, processedSam);

    this.fastaPaths = processedFasta;
    this.bedPaths = processedBed;
    this.samPaths = processedSam;
  }

  private void validate(List<Path> fastaValues, List<Path> bedValues, List<Path> samValues) {
    String errorMessage = "";
    if (fastaValues.size() != 1) errorMessage = FASTA_ARGS_COUNT_EXC;
    else if (bedValues.size() == 0) errorMessage = BED_ARGS_COUNT_EXC;
    else if (samValues.size() == 0) errorMessage = SAM_ARGS_COUNT_EXC;

    else if (someExtensionIsInvalid(fastaValues, ".fasta")) errorMessage = FASTA_EXTENSION_EXC;
    else if (someExtensionIsInvalid(bedValues, ".bed")) errorMessage = BED_EXTENSION_EXC;
    else if (someExtensionIsInvalid(samValues, ".sam")) errorMessage = SAM_EXTENSION_EXC;

    else if (somePathDoesNotExist(fastaValues)) errorMessage = FASTA_PATH_NOT_EXISTS_EXC;
    else if (somePathDoesNotExist(bedValues)) errorMessage = BED_PATH_NOT_EXISTS_EXC;
    else if (somePathDoesNotExist(samValues)) errorMessage = SAM_PATH_NOT_EXISTS_EXC;

    if (!errorMessage.isEmpty()) throw new IllegalArgumentException(errorMessage);
  }

  private boolean somePathDoesNotExist(List<Path> paths) {
    return paths.stream().anyMatch(path -> !Files.exists(path));
  }

  private boolean someExtensionIsInvalid(List<Path> paths, String mustMatch) {
    return paths.stream().anyMatch(path -> !path.toString().endsWith(mustMatch));
  }

  private List<Path> withRemovedDuplicates(List<Path> rawList) {
    return rawList.stream().distinct().collect(Collectors.toList());
  }

  public Path getFastaPath() {
    return fastaPaths.get(0);
  }

  public List<Path> getBedPaths() {
    return Collections.unmodifiableList(bedPaths);
  }

  public List<Path> getSamPaths() {
    return Collections.unmodifiableList(samPaths);
  }
}
