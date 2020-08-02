package com.epam.bioinf.variantcaller.caller;

/**
 * Class holds a subsequence and a read indices.
 */
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

  /**
   * Method moves a subsequence index by n
   *
   * @param n - represents a shift of an index
   */
  public void moveRefIndex(int n) {
    refIndex += n;
  }

  public int getReadIndex() {
    return readIndex;
  }

  /**
   * Method moves a read index by n
   *
   * @param n - represents a shift of an index
   */
  public void moveReadIndex(int n) {
    readIndex += n;
  }
}
