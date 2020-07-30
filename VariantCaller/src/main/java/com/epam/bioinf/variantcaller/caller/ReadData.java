package com.epam.bioinf.variantcaller.caller;

public class ReadData {
  private final byte[] subsequenceBases;
  private final byte[] readBases;
  private final String sampleName;
  private final String contig;
  private final int start;
  private final boolean strandFlag;

  public ReadData(byte[] subsequenceBases, byte[] readBases, String sampleName, String contig, int start, boolean strandFlag) {
    this.subsequenceBases = subsequenceBases;
    this.readBases = readBases;
    this.sampleName = sampleName;
    this.contig = contig;
    this.start = start;
    this.strandFlag = strandFlag;
  }

  public byte[] getSubsequenceBases() {
    return subsequenceBases;
  }

  public byte[] getReadBases() {
    return readBases;
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
