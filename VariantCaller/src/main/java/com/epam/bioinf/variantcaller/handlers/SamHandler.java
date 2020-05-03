package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.RuntimeIOException;
import htsjdk.tribble.bed.BEDFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class implementation is temporary and will be changed in later versions.
 * Class holds paths to SAM files and SAM records. Performs work with them.
 */
public class SamHandler {
  private List<SAMRecord> samRecords;

  /**
   * Constructor gets pre-validated paths to SAM files from ParsedArguments.
   */
  public SamHandler(ParsedArguments parsedArguments) {
    if (parsedArguments.isIntervalsSet()) {
      IntervalsHandler intervalsHandler = new IntervalsHandler(parsedArguments);
      this.samRecords = read(parsedArguments.getSamPaths(), intervalsHandler.getIntervals());
    } else {
      this.samRecords = read(parsedArguments.getSamPaths());
    }

  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private static List<SAMRecord> read(List<Path> samPaths) {
    List<SAMRecord> samRecords = new ArrayList<>();
    SamReaderFactory samFactory = SamReaderFactory.makeDefault();
    for (Path path : samPaths) {
      try (SamReader reader = samFactory.open(path)) {
        for (SAMRecord record : reader) {
          samRecords.add(record);
        }
      } catch (IOException e) {
        throw new RuntimeIOException(e.getMessage(), e.getCause());
      }
    }
    return Collections.unmodifiableList(samRecords);
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private static List<SAMRecord> read(List<Path> samPaths, List<BEDFeature> intervals) {
    List<SAMRecord> samRecords = new ArrayList<>();
    SamReaderFactory samFactory = SamReaderFactory.makeDefault();
    for (Path path : samPaths) {
      try (SamReader reader = samFactory.open(path)) {
        for (SAMRecord record : reader) {
          //System.out.println(record.getContig() + " " +record.getStart() + " " + record.getEnd());
          if (isInsideAnyInterval(record, intervals)) {
            samRecords.add(record);
          }
        }
      } catch (IOException e) {
        throw new RuntimeIOException(e.getMessage(), e.getCause());
      }
    }
    return Collections.unmodifiableList(samRecords);
  }

  private static boolean isInsideAnyInterval(SAMRecord record, List<BEDFeature> intervals) {
    return intervals.stream()
        .anyMatch(interval -> interval.getContig().equals(record.getContig()) &&
            record.getStart() >= interval.getStart() &&
            record.getEnd() <= interval.getEnd());
  }

  private void removeDuplicatedReads() {
    samRecords =
        samRecords.stream().distinct().collect(Collectors.toList());
  }

  public List<SAMRecord> getSamRecords() {
    return samRecords;
  }

  /**
   * Temporary method
   * @return samRecords size
   */
  public int getSamRecordsCount() {
    return samRecords.size();
  }
}
