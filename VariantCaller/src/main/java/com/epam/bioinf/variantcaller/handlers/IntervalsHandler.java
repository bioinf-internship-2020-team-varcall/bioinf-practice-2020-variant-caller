package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.CloseableTribbleIterator;
import htsjdk.tribble.FeatureReader;
import htsjdk.tribble.TribbleException;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.bed.SimpleBEDFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
    intervals = parsedArguments.
        getRegionData().
        map(IntervalsHandler::getIntervalFromRegionData)
        .orElseGet(
            () -> parseIntervalsFromFiles(parsedArguments.getBedPaths()));
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
   * @return list of intervals
   */
  public List<BEDFeature> getIntervals() {
    return intervals;
  }

  private static List<BEDFeature> getIntervalFromRegionData(String region) {
    BEDFeature bedFeature = parseFeatureFromString(region);
    validate(bedFeature);
    return Collections.unmodifiableList(
        List.of(bedFeature));
  }

  /**
   * Getting single interval from string.
   * @param region contains data about start, end and name
   * @return single interval
   */
  private static BEDFeature parseFeatureFromString(String region) {
    Pattern regionSplit = Pattern.compile(" ");
    String[] regionData = regionSplit.split(region);
    String chr = regionData[0];
    int start = parseIntervalPoint(regionData[1]);
    int end = parseIntervalPoint(regionData[2]);
    return new SimpleBEDFeature(start, end, chr);
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private static List<BEDFeature> parseIntervalsFromFiles(List<Path> pathsToFiles) {
    List<BEDFeature> parsedIntervals = new ArrayList<>();
    for (Path path : pathsToFiles) {
      try (
          final FeatureReader<BEDFeature> intervalsReader = AbstractFeatureReader
            .getFeatureReader(path.toString(), new BEDCodec(), false);
          final CloseableTribbleIterator<BEDFeature> iterator = intervalsReader.iterator();
      ) {
        while (iterator.hasNext()) {
          final BEDFeature bedFeature = iterator.next();
          validate(bedFeature);
          parsedIntervals.add(bedFeature);
        }
      } catch (IOException | TribbleException.MalformedFeatureFile exception) {
        throw new RuntimeException(ERROR_READING_EXC, exception);
      }
    }
    return Collections.unmodifiableList(parsedIntervals);
  }

  private static int parseIntervalPoint(String point) {
    try {
      return Integer.parseInt(point);
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException(INVALID_REGION_EXC, exception.getCause());
    }
  }

  private static void validate(BEDFeature bedFeature) {
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
