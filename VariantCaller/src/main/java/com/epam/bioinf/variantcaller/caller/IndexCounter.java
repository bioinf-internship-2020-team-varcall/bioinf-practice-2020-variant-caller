package com.epam.bioinf.variantcaller.caller;

public class IndexCounter {
  private int refIndex;
  private int readIndex;

  public IndexCounter(int refIndex, int readIndex) {
    this.refIndex = refIndex;
    this.readIndex = readIndex;
  }

  public int getRefIndex() {
    return refIndex;
  }

  public void moveRefIndex(int n) {
    refIndex += n;
  }

  public int getReadIndex() {
    return readIndex;
  }

  public void moveReadIndex(int n) {
    readIndex += n;
  }
}
