package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.Arrays;

public class AlleleData {
  public int[] countStrands;

  public AlleleData(Allele allele) {
    countStrands = new int[]{0, 0};
  }

  public void incrementStrandCount(boolean isReversed) {
    countStrands[isReversed ? 1 : 0]++;
  }

  public int count() {
    return countStrands[0] + countStrands[1];
  }

  @Override
  public String toString() {
    return "AlleleData{" +
        "countStrands=" + Arrays.toString(countStrands) +
        '}';
  }
}
