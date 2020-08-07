package com.epam.bioinf.variantcaller.caller;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;

/**
 * Contains all the read and subsequence information related to one sam record.
 */
public class ReadData {
  private final String subsequenceBaseString;
  private final SAMRecord samRecord;

  public ReadData(
      String subsequenceBaseString,
      SAMRecord samRecord
  ) {
    this.subsequenceBaseString = subsequenceBaseString;
    this.samRecord = samRecord;
  }

  public final String getSubsequenceBaseString() {
    return subsequenceBaseString;
  }

  public final String getReadBaseString() {
    return samRecord.getReadString();
  }

  public String getSampleName() {
    return samRecord.getAttribute(SAMTag.SM.name()).toString();
  }

  public byte getBaseQualityAtPosition(int i) {
    return samRecord.getBaseQualities()[i];
  }

  public String getContig() {
    return samRecord.getContig();
  }

  public int getStart() {
    return samRecord.getStart();
  }

  public int getMappingQuality() {
    return samRecord.getMappingQuality();
  }

  public boolean getReadNegativeStrandFlag() {
    return samRecord.getReadNegativeStrandFlag();
  }
}
