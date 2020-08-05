package com.epam.bioinf.variantcaller.caller;

/**
 * Contains all the read and subsequence information related to one sam record.
 */
public class ReadData {
  private final String subsequenceBaseString;
  private final String readBaseString;
  private final String sampleName;
  private final String contig;
  private final int start;
  private final boolean strandFlag;

  public ReadData(
      String subsequenceBaseString,
      String readBaseString,
      String sampleName,
      String contig,
      int start,
      boolean strandFlag
  ) {
    this.subsequenceBaseString = subsequenceBaseString;
    this.readBaseString = readBaseString;
    this.sampleName = sampleName;
    this.contig = contig;
    this.start = start;
    this.strandFlag = strandFlag;
  }

  public final String getSubsequenceBaseString() {
    return subsequenceBaseString;
  }

  public final String getReadBaseString() {
    return readBaseString;
  }

  public String getSampleName() {
    return sampleName;
  }

  public String getContig() {
    return contig;
  }

  public int getStart() {
    return start;
  }

  public boolean getReadNegativeStrandFlag() {
    return strandFlag;
  }
}
