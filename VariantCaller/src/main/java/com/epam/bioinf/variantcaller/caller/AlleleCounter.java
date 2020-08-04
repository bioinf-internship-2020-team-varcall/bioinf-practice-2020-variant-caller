package com.epam.bioinf.variantcaller.caller;

/**
 * Holds counts of alleles on forward and reversed strands.
 */
public class AlleleCounter {
  private int forwardStrandCnt;
  private int reversedStrandCnt;

  public AlleleCounter() {
    forwardStrandCnt = 0;
    reversedStrandCnt = 0;
  }

  /**
   * Increments forward or reversed strand allele counts based on flag.
   *
   * @param isReversed - flag which represents a condition whether a strand reversed
   */
  public void incrementStrandCount(boolean isReversed) {
    if (isReversed) {
      reversedStrandCnt++;
    } else {
      forwardStrandCnt++;
    }
  }

  /**
   * Returns sum of alleles on each strand
   */
  public int count() {
    return forwardStrandCnt + reversedStrandCnt;
  }

  public int getForwardStrandCnt() {
    return forwardStrandCnt;
  }

  public int getReversedStrandCnt() {
    return reversedStrandCnt;
  }

  @Override
  public String toString() {
    return "AlleleCounter{" +
        "forwardStrandCnt=" + forwardStrandCnt +
        ", reversedStrandCnt=" + reversedStrandCnt +
        '}';
  }
}
