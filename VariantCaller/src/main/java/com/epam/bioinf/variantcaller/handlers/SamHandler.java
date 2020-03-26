package com.epam.bioinf.variantcaller.handlers;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static com.epam.bioinf.variantcaller.helpers.exceptions.messages.SamHandlerMessages.SAM_FILES_PATHS_LIST_EMPTY;
import static com.epam.bioinf.variantcaller.helpers.exceptions.messages.SamHandlerMessages.SAM_FILE_CONTAINS_ONLY_ONE_READ;

public class SamHandler {

  private List<Path> samPaths;
  private SamReaderFactory samFactory = SamReaderFactory.makeDefault()
      .enable(SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS)
      .validationStringency(ValidationStringency.LENIENT);

  public SamHandler(List<Path> samPaths) {
    if (samPaths.isEmpty()) {
      throw new IllegalArgumentException(SAM_FILES_PATHS_LIST_EMPTY);
    }
    this.samPaths = samPaths;
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
      throw new IllegalArgumentException(SAM_FILE_CONTAINS_ONLY_ONE_READ + samPath);
    } else {
      return readsNumber;
    }
  }

}
