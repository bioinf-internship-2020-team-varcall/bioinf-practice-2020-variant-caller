package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.region.RegionIllegalEndException;
import com.epam.bioinf.variantcaller.exceptions.handlers.region.RegionIllegalIntervalException;
import com.epam.bioinf.variantcaller.exceptions.handlers.region.RegionIllegalStartException;
import com.epam.bioinf.variantcaller.exceptions.handlers.region.RegionReadingException;
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
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class reads and stores intervals for VariantCaller.
 * Current implementation may differ from final version.
 */
public class IntervalsHandler {
  private static final Pattern regionSplit = Pattern.compile(" ");

  /**
   * Method reads region information and depending on it
   * creates a single interval or parses multiple from files
   * and returns them.
   *
   * @param parsedArguments with region information.
   * @see ParsedArguments
   */
  public static List<BEDFeature> getIntervals(ParsedArguments parsedArguments) {
    return parsedArguments
        .getRegionData()
        .map(IntervalsHandler::getIntervalFromRegionData)
        .orElseGet(
            () -> parseIntervalsFromFiles(parsedArguments.getBedPaths()));
  }

  private static List<BEDFeature> getIntervalFromRegionData(String region) {
    BEDFeature bedFeature = parseFeatureFromString(region);
    validate(bedFeature);
    return Collections.unmodifiableList(
        List.of(bedFeature));
  }

  /**
   * Getting single interval from string.
   *
   * @param region contains data about start, end and name.
   * @return single interval.
   */
  private static BEDFeature parseFeatureFromString(String region) {
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
              .getFeatureReader(path.toString(), new BEDCodec(BEDCodec.StartOffset.ZERO), false);
          final CloseableTribbleIterator<BEDFeature> iterator = intervalsReader.iterator();
      ) {
        while (iterator.hasNext()) {
          final BEDFeature bedFeature = iterator.next();
          validate(bedFeature);
          parsedIntervals.add(bedFeature);
        }
      } catch (IOException | TribbleException.MalformedFeatureFile exception) {
        throw new RegionReadingException(exception);
      }
    }
    if (parsedIntervals.size() >= 2) {
      parsedIntervals = mergeOverlappingIntervals(parsedIntervals);
    }
    return Collections.unmodifiableList(parsedIntervals);
  }

  private static int parseIntervalPoint(String point) {
    try {
      return Integer.parseInt(point);
    } catch (NumberFormatException exception) {
      throw new RegionIllegalIntervalException(exception);
    }
  }

  private static void validate(BEDFeature bedFeature) {
    final int start = bedFeature.getStart();
    final int end = bedFeature.getEnd();
    if (start < 1) {
      throw new RegionIllegalStartException();
    } else if (end < 1 || end < start - 1) {
      throw new RegionIllegalEndException();
    }
  }

  /**
   * Method checks if provided intervals have overlaps and
   * if such are present constructs new one from them.
   *
   * @param intervals with possible overlapping intervals
   * @return intervals with merged overlapping intervals
   */
  private static List<BEDFeature> mergeOverlappingIntervals(List<BEDFeature> intervals) {
    Comparator<BEDFeature> intervalsComparator = Comparator
        .comparing(BEDFeature::getContig)
        .thenComparing(BEDFeature::getStart);

    List<BEDFeature> sortedIntervals = intervals.stream()
        .sorted(intervalsComparator)
        .collect(Collectors.toList());

    List<BEDFeature> verifiedIntervals = new ArrayList<>();
    Iterator<BEDFeature> iterator = sortedIntervals.iterator();
    BEDFeature current = iterator.next();

    while (iterator.hasNext()) {
      BEDFeature next = iterator.next();
      if (current.getContig().equals(next.getContig())) {
        if (current.getEnd() >= next.getStart()) {
          current = new SimpleBEDFeature(
              current.getStart(),
              Math.max(current.getEnd(), next.getEnd()),
              current.getContig());
          if (!iterator.hasNext()) {
            break;
          }
          continue;
        }
      }
      verifiedIntervals.add(current);
      current = next;
    }
    verifiedIntervals.add(current);
    return verifiedIntervals;
  }
}
