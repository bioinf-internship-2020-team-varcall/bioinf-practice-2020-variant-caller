package com.epam.bioinf.variantcaller.caller.position;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds counts of alleles on forward and reversed strands.
 */
public class PositionStatistics {
  private int forwardStrandCnt;
  private int reversedStrandCnt;
  private final List<Integer> baseQs;
  private final List<Integer> mapQs;

  public PositionStatistics() {
    forwardStrandCnt = 0;
    reversedStrandCnt = 0;
    baseQs = new ArrayList<>();
    mapQs = new ArrayList<>();
  }

  /**
   * Increments forward or reversed strand allele counts based on flag.
   *
   * @param isReversed - flag which represents a condition whether a strand reversed
   */
  public PositionStatistics incrementStrandCount(boolean isReversed) {
    if (isReversed) {
      reversedStrandCnt++;
    } else {
      forwardStrandCnt++;
    }
    return this;
  }

  /**
   * Returns sum of alleles on each strand
   */
  public int count() {
    return forwardStrandCnt + reversedStrandCnt;
  }

  public int getForwardStrandCount() {
    return forwardStrandCnt;
  }

  public int getReversedStrandCount() {
    return reversedStrandCnt;
  }

  public PositionStatistics addMapQ(int mapQ) {
    mapQs.add(mapQ);
    return this;
  }

  public PositionStatistics addBaseQ(int refBaseMapQ) {
    baseQs.add(refBaseMapQ);
    return this;
  }

  public List<Integer> getBaseQs() {
    return baseQs;
  }

  public List<Integer> getMapQs() {
    return mapQs;
  }

  @Override
  public String toString() {
    return "PositionStatistics{" +
        "forwardStrandCnt=" + forwardStrandCnt +
        ", reversedStrandCnt=" + reversedStrandCnt +
        '}';
  }
}
