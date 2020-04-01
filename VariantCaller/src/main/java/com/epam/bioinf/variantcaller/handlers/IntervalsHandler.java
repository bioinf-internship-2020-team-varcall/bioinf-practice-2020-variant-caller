package com.epam.bioinf.variantcaller.handlers;

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

/**
 * TODO javadoc
 */
public class IntervalsHandler {

  private List<BEDFeature> intervals;

  /**
   * TODO javadoc
   * @param intervalData
   */
  public IntervalsHandler(String intervalData) {

    intervals = new ArrayList<>();

    String[] data = intervalData.split(" ");

    // TODO custom error message if parsing fails
    String chr = data[0];
    int start = Integer.parseInt(data[1]);
    int end = Integer.parseInt(data[2]);

    BEDFeature bedFeature = new SimpleBEDFeature(start, end, chr);
    validate(bedFeature);
    intervals.add(bedFeature);
  }

  /**
   * TODO javadoc
   * @param pathsToFiles
   */
  public IntervalsHandler(List<Path> pathsToFiles) {

    intervals = new ArrayList<>();
    //TODO check if file can be decoded
    pathsToFiles.forEach(path -> {
      try {
        final FeatureReader<BEDFeature> intervalsReader = AbstractFeatureReader
            .getFeatureReader(path.toString(), new BEDCodec(), false);
        final CloseableTribbleIterator<BEDFeature> iterator = intervalsReader.iterator();

        while (iterator.hasNext()) {
          final BEDFeature bedFeature = iterator.next();

          validate(bedFeature);

          intervals.add(bedFeature);

        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

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
   * @return unmodifiable list of intervals
   */
  public List<BEDFeature> getIntervals() {
    return Collections.unmodifiableList(intervals);
  }

  private void validate(BEDFeature bedFeature) {

    final int start = bedFeature.getStart();
    final int end = bedFeature.getEnd();

    // Validation
    // TODO: errors

    if (start < 1) {
      System.out.println("error");
    } else if (end < 1) {
      System.out.println("error");
    } else if (end < start - 1) {
      System.out.println("error");
    }
  }

}
