package com.epam.bioinf.variantcaller.caller;

import htsjdk.samtools.CigarElement;

/**
 * Holds a subsequence and a read indices.
 * Difference between indices might appear
 * when CIGAR contains indel operations as
 * well as alignment ones
 *
 * @see Caller#processSingleCigarElement
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
   * Moves a subsequence and a read indices.
   *
   * @param refIndexShift  - represents a shift of a reference index
   * @param readIndexShift - represents a shift of a read index
   */
  public void moveIndices(int refIndexShift, int readIndexShift) {
    refIndex += refIndexShift;
    readIndex += readIndexShift;
  }

  public int getReadIndex() {
    return readIndex;
  }
}
