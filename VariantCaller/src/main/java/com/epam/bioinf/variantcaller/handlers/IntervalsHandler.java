package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.CloseableTribbleIterator;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.bed.SimpleBEDFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.epam.bioinf.variantcaller.helpers.exceptions.messages.IntervalsHandlerMessages.*;

/**
 * Class reads and stores intervals for VariantCaller.
 * Current implementation may differ from final version.
 */
public class IntervalsHandler {

  private List<BEDFeature> intervals;

  /**
   * Constructor reads region information and depending on it
   * creates a single interval or parses multiple from files
   * and stores them.
   *
   * @param parsedArguments with region information
   * @see ParsedArguments
   */
  public IntervalsHandler(ParsedArguments parsedArguments) {

    intervals = new ArrayList<>();

    if (parsedArguments.getRegionData() != null) {
      parseIntervalFromRegionData(parsedArguments);
    } else {
      parseIntervalsFromFiles(parsedArguments);
    }
  }

  /**
   * Temporary method to check class functionality.
   * Prints list of intervals to stdout.
   */
  public void listIntervals() {
    intervals.stream().map(BEDFeature::getContig).forEach(System.out::println);
  }

  /**
   * Returns stored intervals.
   *
   * @return unmodifiable list of intervals
   */
  public List<BEDFeature> getIntervals() {
    return Collections.unmodifiableList(intervals);
  }

  private void parseIntervalFromRegionData(ParsedArguments parsedArguments) {

    String[] data = parsedArguments.getRegionData().split(" ");

    String chr = data[0];
    int start = tryParseIntervalPoint(data[1]);
    int end = tryParseIntervalPoint(data[2]);

    BEDFeature bedFeature = new SimpleBEDFeature(start, end, chr);
    validate(bedFeature);
    intervals.add(bedFeature);
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private void parseIntervalsFromFiles(ParsedArguments parsedArguments) {

    List<Path> pathsToFiles = parsedArguments.getBedPaths();

    for (Path path : pathsToFiles) {
      try (final FeatureReader<BEDFeature> intervalsReader = AbstractFeatureReader
          .getFeatureReader(path.toString(), new BEDCodec(), false);
           final CloseableTribbleIterator<BEDFeature> iterator = intervalsReader.iterator();) {
        while (iterator.hasNext()) {
          final BEDFeature bedFeature = iterator.next();
          validate(bedFeature);
          intervals.add(bedFeature);
        }
      } catch (IOException e) {
        throw new RuntimeException(ERROR_READING_EXC, e.getCause());
      }
    }
  }

  private int tryParseIntervalPoint(String point) {
    try {
      return Integer.parseInt(point);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(INVALID_REGION_EXC, e.getCause());
    }
  }

  private void validate(BEDFeature bedFeature) {

    final int start = bedFeature.getStart();
    final int end = bedFeature.getEnd();
    String errorMessage = "";

    if (start < 1) {
      errorMessage = INTERVAL_START_EXC;
    } else if (end < 1 || end < start - 1) {
      errorMessage = INTERVAL_END_EXC;
    }

    if (!errorMessage.isEmpty()) {
      throw new IllegalArgumentException(errorMessage);
    }
  }
}
