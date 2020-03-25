package com.epam.bioinf.variantcaller.handlers;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class SamHandler {

  private List<Path> samPaths;
  private SamReaderFactory samFactory = SamReaderFactory.makeDefault()
      .enable(SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS)
      .validationStringency(ValidationStringency.LENIENT);

  public SamHandler(List<Path> samPaths) {
    this.samPaths = samPaths;
  }

  public Map<Path, Long> computeReadsByPaths() {
    Map<Path, Long> readsByPathsMap = new HashMap<>();
    samPaths.forEach(path -> readsByPathsMap.put(path, countReadsIn(path)));
    return readsByPathsMap;
  }

  private long countReadsIn(Path samPath) {
    SamReader reader = samFactory.open(samPath);
    return StreamSupport.stream(reader.spliterator(), true).count();
  }
}
