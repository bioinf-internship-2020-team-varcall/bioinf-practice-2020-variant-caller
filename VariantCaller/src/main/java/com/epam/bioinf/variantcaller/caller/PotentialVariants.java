package com.epam.bioinf.variantcaller.caller;

import java.util.ArrayList;
import java.util.List;

public class PotentialVariants {
  private final Byte refChar;
  private final List<Byte> potentialVariants;

  PotentialVariants(Byte refChar) {
    this.refChar = refChar;
    potentialVariants = new ArrayList<>();
  }

  public void addPotentialVariant(Byte potentialVariant) {
    potentialVariants.add(potentialVariant);
  }

  public List<Byte> getPotentialVariants() {
    return potentialVariants;
  }

  public byte getRefChar() {
    return refChar;
  }

  public Long[] getVariants() {
    //0 -> "A" 1 -> "C" 2 -> "T" 3 -> "G"
    Long[] result = {0L, 0L, 0L, 0L};
    potentialVariants.forEach(pv -> {
      switch (pv) {
        case (byte) 'A':
          result[0]++;
          break;
        case (byte) 'C':
          result[1]++;
          break;
        case (byte) 'T':
          result[2]++;
          break;
        case (byte) 'G':
          result[3]++;
          break;
      }
    });
    return result;
  }

  @Override
  public String toString() {
    return "Variant{" +
        "refChar=" + refChar +
        ", potentialVariants=" + potentialVariants +
        '}';
  }
}