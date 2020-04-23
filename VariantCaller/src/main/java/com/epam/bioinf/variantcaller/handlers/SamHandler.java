package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class implementation is temporary and will be changed in later versions.
 * Class holds paths to SAM files and SAM records. Performs work with them.
 */
public class SamHandler {
  private List<Path> samPaths;
  private List<SAMRecord> samRecords;
  private SamReaderFactory samFactory;
  private Map<Path, Long> readsNumberByPath;

  /**
   * Constructor gets pre-validated paths to SAM files from ParsedArguments.
   */
  public SamHandler(ParsedArguments parsedArguments) {
    this.samPaths = parsedArguments.getSamPaths();
    this.samRecords = new ArrayList<>();
    this.samFactory = SamReaderFactory.makeDefault();
    this.readsNumberByPath = new HashMap<>();
    read();
    removeDuplicatedReads();
  }

  /**
   * This method is temporary and will be removed in later versions.
   *
   * @return Map of provided files & number of reads in each file.
   */
  public Map<Path, Long> getReadsNumberByPath() {
    return readsNumberByPath;
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private void read() {
    for (Path path : samPaths) {
      try (SamReader reader = samFactory.open(path)) {
        long counter = 0;
        for (SAMRecord record : reader) {
          samRecords.add(record);
          counter++;
        }
        readsNumberByPath.put(path, counter);
      } catch (IOException e) {
        throw new RuntimeIOException(e.getMessage(), e);
      }
    }
  }

  private void removeDuplicatedReads() {
    samRecords =
        samRecords.stream().distinct().collect(Collectors.toList());
  }

  public List<SAMRecord> getSamRecords() {
    return samRecords;
  }

}
