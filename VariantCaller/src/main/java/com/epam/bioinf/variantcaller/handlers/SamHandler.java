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
import java.util.stream.StreamSupport;

import static com.epam.bioinf.variantcaller.helpers.exceptions.messages.SamHandlerMessages.SAM_FILE_CONTAINS_ONLY_ONE_READ_EXC;

/**
 * Class holds paths to SAM files and SAM records. Performs work with them.
 */
public class SamHandler {

  private List<Path> samPaths;
  private ArrayList<SAMRecord> samRecords = new ArrayList<>();
  private SamReaderFactory samFactory = SamReaderFactory.makeDefault();

  /**
   * Constructor checks gets and holds pre-validated paths to SAM files from ParsedArguments.
   */
  public SamHandler(ParsedArguments parsedArguments) {
    this.samPaths = parsedArguments.getSamPaths();
    removeDuplicatedSequences();
  }

  //dumb method
  private void removeDuplicatedSequences() {
    System.out.println(samRecords.stream().map(SAMRecord::getReadString).distinct().count());
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

  private long countReadsIn(Path samPath) {
    SamReader reader = samFactory.open(samPath);
    long readsNumber = StreamSupport.stream(reader.spliterator(), true).count();
    if (readsNumber == 1) {
      throw new IllegalArgumentException(SAM_FILE_CONTAINS_ONLY_ONE_READ_EXC + samPath);
    } else {
      return readsNumber;
    }
  }

  public ArrayList<SAMRecord> getSamRecords() {
    return samRecords;
  }

}
