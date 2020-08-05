package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.sam.SamNoRelatedReadsException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import htsjdk.samtools.*;
import htsjdk.samtools.util.RuntimeIOException;
import htsjdk.tribble.bed.BEDFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class implementation is temporary and will be changed in later versions.
 * Class reads and holds SAM records. Reading is different depending if
 * intervals are given. If given, getting intervals data from IntervalsHandler.
 *
 * @see IntervalsHandler
 */
public class SamHandler {
  private List<SAMRecord> samRecords;

  /**
   * Constructor gets pre-validated paths to SAM files from ParsedArguments.
   */
  public SamHandler(ParsedArguments parsedArguments) {
    List<BEDFeature> intervals = IntervalsHandler.getIntervals(parsedArguments);
    this.samRecords = read(parsedArguments.getSamPaths(), intervals);
    removeDuplicatedReads();
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private static List<SAMRecord> read(List<Path> samPaths, List<BEDFeature> intervals) {
    boolean hasEmptyIntervals = intervals.isEmpty();
    List<SAMRecord> samRecords = new ArrayList<>();
    SamReaderFactory samFactory = SamReaderFactory.makeDefault();
    for (Path path : samPaths) {
      try (SamReader reader = samFactory.open(path)) {
        for (SAMRecord record : reader) {
          if (hasEmptyIntervals || isInsideAnyInterval(record, intervals)) {
            Optional<SAMReadGroupRecord> readGroup = reader
                .getFileHeader()
                .getReadGroups()
                .stream().filter(gr -> gr.getId().equals(record.getAttribute(SAMTag.RG.name())))
                .findFirst();
            readGroup.ifPresent(samReadGroupRecord -> {
              record.setAttribute(SAMTag.SM.name(), samReadGroupRecord.getSample());
            });
            samRecords.add(record);
          }
        }
      } catch (IOException e) {
        throw new RuntimeIOException(e.getMessage(), e);
      }
    }
    if (samRecords.isEmpty()) {
      throw new SamNoRelatedReadsException();
    }
    return Collections.unmodifiableList(samRecords);
  }

  /**
   * Method checks if input record corresponds to any given interval.
   *
   * @param record    - SAMRecord
   * @param intervals - list of intervals
   * @return true if record is located on same contig and in the interval range
   */
  private static boolean isInsideAnyInterval(SAMRecord record, List<BEDFeature> intervals) {
    return intervals.stream()
        .anyMatch(interval -> interval.getContig().equals(record.getContig()) &&
            record.getStart() >= interval.getStart() &&
            record.getEnd() <= interval.getEnd());
  }

  private void removeDuplicatedReads() {
    samRecords = samRecords.stream().distinct().collect(Collectors.toList());
  }

  public List<SAMRecord> getSamRecords() {
    return samRecords;
  }

  /**
   * Temporary method for testing.
   *
   * @return samRecords size
   */
  public int getSamRecordsCount() {
    return samRecords.size();
  }
}
