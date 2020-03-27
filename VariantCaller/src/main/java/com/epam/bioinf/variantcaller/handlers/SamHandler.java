package com.epam.bioinf.variantcaller.handlers;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.epam.bioinf.variantcaller.helpers.exceptions.messages.SamHandlerMessages.*;

public class SamHandler {

  private List<Path> samPaths;
  private SamReaderFactory samFactory = SamReaderFactory.makeDefault();
  private ArrayList<SAMRecord> samRecords = new ArrayList<>();

  public SamHandler(List<Path> samPaths) {
    validate(samPaths);
    this.samPaths = withRemovedDuplicates(samPaths);
    removeDuplicatedSequences();//bred
  }

  //dumb method
  private void removeDuplicatedSequences() {
    System.out.println(samRecords.stream().map(SAMRecord::getReadString).distinct().count());
  }


  public Map<Path, Long> countReadsByPath() {
    Map<Path, Long> readsByPathMap = new HashMap<>();
    samPaths.forEach(path -> readsByPathMap.put(path, countReadsIn(path)));
    return readsByPathMap;
  }

  private long countReadsIn(Path samPath) {
    SamReader reader = samFactory.open(samPath);
    long readsNumber = StreamSupport.stream(reader.spliterator(), true).count();
    if (readsNumber == 1) {
      throw new IllegalArgumentException(SAM_FILE_CONTAINS_ONLY_ONE_READ_EXC + samPath);
    } else {
      return readsNumber;
    }
  }

  private List<Path> withRemovedDuplicates(List<Path> rawList) {
    return rawList.stream().distinct().collect(Collectors.toList());
  }

  private boolean pathDoesNotExist(List<Path> paths) {
    return paths.stream().anyMatch(path -> !Files.exists(path));
  }

  private boolean extensionIsInvalid(List<Path> paths) {
    return paths.stream().anyMatch(path -> !path.toString().endsWith(".sam"));
  }

  private void validate(List<Path> paths) {
    String errorMessage = "";
    if (paths.isEmpty()) errorMessage = SAM_FILES_PATHS_LIST_EMPTY_EXC;
    else if (extensionIsInvalid(paths)) errorMessage = SAM_INVALID_EXTENSION_EXC;
    else if (pathDoesNotExist(paths)) errorMessage = SAM_PATH_NOT_EXISTS_EXC;

    if (!errorMessage.isEmpty()) throw new IllegalArgumentException(errorMessage);
  }

}
