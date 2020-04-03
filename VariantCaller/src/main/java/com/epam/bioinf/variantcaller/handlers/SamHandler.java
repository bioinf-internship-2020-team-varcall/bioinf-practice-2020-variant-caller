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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
   */
  public Map<Path, Long> countReadsByPath() {
    return samPaths.stream().collect(Collectors.toMap(Function.identity(), this::countReadsIn));
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private void read() {
    samPaths.forEach( path -> {
      try (SamReader reader = samFactory.open(path)) {
        reader.forEach(samRecords::add);
      } catch (RuntimeIOException | IOException e) {
        throw new RuntimeIOException(e.getMessage());
      }
    });
  }

  private void removeDuplicatedReads() {
    samRecords =
        samRecords.stream().distinct().collect(Collectors.toList());
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private long countReadsIn(Path samPath) {
    try (SamReader reader = samFactory.open(samPath)) {
      return StreamSupport.stream(reader.spliterator(), true).count();
    } catch (RuntimeIOException | IOException e) {
      throw new RuntimeIOException(e.getMessage());
    }
  }

  public List<SAMRecord> getSamRecords() {
    return samRecords;
  }

}
