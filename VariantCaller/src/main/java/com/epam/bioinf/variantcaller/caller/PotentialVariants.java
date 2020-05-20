package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.*;
import java.util.stream.Collectors;

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

  public Byte getRefChar() {
    return refChar;
  }

  public Allele getRefAllele() {
    return Allele.create(refChar, true);
  }

  public ArrayList<Allele> getVariants() {
    HashMap<Integer, Byte> indexToByte = new HashMap<>();
    indexToByte.put(0, (byte)'A');
    indexToByte.put(1, (byte)'C');
    indexToByte.put(2, (byte)'G');
    indexToByte.put(3, (byte)'T');

    Long[] res = {0L, 0L, 0L, 0L};
    potentialVariants.forEach(pv -> {
      switch (pv) {
        case (byte) 'A':
          res[0]++;
          break;
        case (byte) 'C':
          res[1]++;
          break;
        case (byte) 'G':
          res[2]++;
          break;
        case (byte) 'T':
          res[3]++;
          break;
      }
    });
    ArrayList<Allele> variants = new ArrayList<>();

    var listOfCounts = Arrays.asList(res);
    var max = Collections.max(listOfCounts);
    for (int i = 0; i < 4; i++) {
      if (max.equals(listOfCounts.get(i)) && listOfCounts.get(i) >= 2) {
        variants.add(Allele.create(indexToByte.get(i)));
      }
    }

    return variants;
  }

  @Override
  public String toString() {
    return "Variant{" +
        "refChar=" + refChar +
        ", potentialVariants=" + potentialVariants +
        '}';
  }
}
