package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Class implementation is temporary and will be changed in later versions.
 * Class holds paths to SAM files and SAM records. Performs work with them.
 */
public class SamHandler {
  private List<Path> samPaths;
  private List<SAMRecord> samRecords;
  private SamReaderFactory samFactory;

  /**
   * Constructor gets pre-validated paths to SAM files from ParsedArguments.
   */
  public SamHandler(ParsedArguments parsedArguments) {
    this.samPaths = parsedArguments.getSamPaths();
    this.samRecords = new ArrayList<>();
    this.samFactory = SamReaderFactory.makeDefault();
    read();
    removeDuplicatedReads();
  }

  /**
   * This method is temporary and will be removed in later versions.
   *
   * @return Map of provided files & number of reads in each file.
   * @throws IllegalArgumentException if file contains only one read.
   */
  public Map<Path, Long> countReadsByPath() {
    Map<Path, Long> readsByPathMap = new HashMap<>();
    samPaths.forEach(path -> readsByPathMap.put(path, countReadsIn(path)));
    return readsByPathMap;
  }

  private void read() {
    samPaths.forEach(path -> {
      SamReader reader = samFactory.open(path);
      reader.forEach(samRecords::add);
    });
  }

  private void removeDuplicatedReads() {
    samRecords =
        samRecords.stream().distinct().collect(Collectors.toList());
  }

  private long countReadsIn(Path samPath) {
    SamReader reader = samFactory.open(samPath);
    return StreamSupport.stream(reader.spliterator(), true).count();
  }

  public List<SAMRecord> getSamRecords() {
    return samRecords;
  }

}
