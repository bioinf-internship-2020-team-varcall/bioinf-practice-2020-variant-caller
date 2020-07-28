package com.epam.bioinf.variantcaller.cmdline;

import com.epam.bioinf.variantcaller.exceptions.ParserException;
import com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaArgsSizeException;
import com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaInvalidExtensionException;
import com.epam.bioinf.variantcaller.exceptions.parser.fasta.FastaPathNotExistsException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionInvalidException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionInvalidExtensionException;
import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionPathNotExistsException;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamArgsSizeException;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamInvalidExtensionException;
import com.epam.bioinf.variantcaller.exceptions.parser.sam.SamPathNotExistsException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class holds validated data
 */
public class ParsedArguments {
  private final Path fastaPath;
  private final List<Path> bedPaths;
  private final List<Path> samPaths;
  private final Optional<String> regionData;
  private static final Pattern regionSplit = Pattern.compile(" ");

  /**
   * Constructor validates parsed arguments
   */
  public ParsedArguments(List<Path> fastaPaths, List<Path> bedPaths,
                         List<Path> samPaths, Optional<String> regionData) {
    List<Path> processedFasta = withRemovedDuplicates(fastaPaths);
    List<Path> processedBed = withRemovedDuplicates(bedPaths);
    List<Path> processedSam = withRemovedDuplicates(samPaths);

    validate(processedFasta, processedBed, processedSam, regionData);

    this.fastaPath = processedFasta.get(0);
    this.bedPaths = processedBed;
    this.samPaths = processedSam;
    this.regionData = regionData;
  }

  private void validate(List<Path> fastaValues, List<Path> bedValues,
                        List<Path> samValues, Optional<String> regionData) {
    if (fastaValues.size() != 1) {
      throw new FastaArgsSizeException();
    } else if (bedValues.isEmpty()) {
      regionData.ifPresent(this::checkIfRegionDataIsInvalid);
    } else if (samValues.isEmpty()) {
      throw new SamArgsSizeException();
    } else if (checkIfSomeExtensionsIsInvalid(fastaValues, AllowedExtensions.FASTA_EXTENSIONS)) {
      throw new FastaInvalidExtensionException();
    } else if (checkIfSomeExtensionsIsInvalid(bedValues, AllowedExtensions.BED_EXTENSIONS)) {
      throw new RegionInvalidExtensionException();
    } else if (checkIfSomeExtensionsIsInvalid(samValues, AllowedExtensions.SAM_EXTENSIONS)) {
      throw new SamInvalidExtensionException();
    } else if (checkIfSomePathDoesNotExist(fastaValues)) {
      throw new FastaPathNotExistsException();
    } else if (checkIfSomePathDoesNotExist(bedValues)) {
      throw new RegionPathNotExistsException();
    } else if (checkIfSomePathDoesNotExist(samValues)) {
      throw new SamPathNotExistsException();
    }
  }

  private boolean checkIfSomePathDoesNotExist(List<Path> paths) {
    return paths.stream().anyMatch(path -> !Files.exists(path));
  }

  private boolean checkIfSomeExtensionsIsInvalid(List<Path> paths, List<String> allowedExts) {
    if (paths.stream().allMatch(path -> path.toString().contains("."))) {
      return paths
          .stream()
          .map(path -> path.toString().substring(path.toString().lastIndexOf(".") + 1))
          .anyMatch(ext -> !allowedExts.contains(ext));
    }
    return true;
  }

  /**
   * Checking that region from input is valid by dividing string
   * input and validating size of array is 3. Example:
   * "chr1 1 10" -> ["chr1", "1", "10"] - valid
   * "chr1 1 10 1" -> ["chr1", "1", "10", "1"] - invalid
   *
   * @param regionData region info
   */
  private void checkIfRegionDataIsInvalid(String regionData) {
    if (regionSplit.split(regionData).length != 3) {
      throw new RegionInvalidException();
    }
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

  public Optional<String> getRegionData() {
    return regionData;
  }

  /**
   * Class holds allowed extensions
   */
  private static final class AllowedExtensions {
    private AllowedExtensions() {
      // restrict instantiation
    }

    private static final List<String> FASTA_EXTENSIONS = List.of("fasta", "fna", "fa");
    private static final List<String> BED_EXTENSIONS = List.of("bed");
    private static final List<String> SAM_EXTENSIONS = List.of("sam");
  }
}
