package com.epam.bioinf.variantcaller.caller;

public class AlleleCounter {
  private int forwardStrandCnt;
  private int reversedStrandCnt;

  public AlleleCounter() {
    forwardStrandCnt = 0;
    reversedStrandCnt = 0;
  }

  public void incrementStrandCount(boolean isReversed) {
    if (isReversed) {
      reversedStrandCnt++;
    } else {
      forwardStrandCnt++;
    }
  }

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
