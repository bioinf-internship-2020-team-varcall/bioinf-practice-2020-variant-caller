package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VariantContext {
  private final Byte refChar;
  private final List<Byte> potentialVariants;

  VariantContext(Byte refChar) {
    this.refChar = refChar;
    potentialVariants = new ArrayList<>();
  }

  public void addPotentialVariant(Byte potentialVariant) {
    potentialVariants.add(potentialVariant);
  }

  public Byte getRefChar() {
    return refChar;
  }

  public Allele getRefAllele() {
    return Allele.create(refChar, true);
  }

  public ArrayList<Allele> getVariants() {
    List<Byte> nonRefLetters = potentialVariants
        .stream()
        .filter(potentialVariant -> !potentialVariant.equals(refChar))
        .collect(Collectors.toList());
    char[] baseLetters = {'A', 'C', 'G', 'T'};
    Long[] listOfCounts = {0L, 0L, 0L, 0L};
    nonRefLetters.forEach(pv -> {
      switch (pv) {
        case (byte) 'A':
          listOfCounts[0]++;
          break;
        case (byte) 'C':
          listOfCounts[1]++;
          break;
        case (byte) 'G':
          listOfCounts[2]++;
          break;
        case (byte) 'T':
          listOfCounts[3]++;
          break;
        default:
          //ignore because current version supports only DNA sequences
          break;
      }
    });
    ArrayList<Allele> variants = new ArrayList<>();
    var max = Collections.max(Arrays.asList(listOfCounts));
    if (max >= 2) {
      for (int i = 0; i < 4; i++) {
        if (max.equals(listOfCounts[i])) {
          variants.add(Allele.create((byte) baseLetters[i]));
        }
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
