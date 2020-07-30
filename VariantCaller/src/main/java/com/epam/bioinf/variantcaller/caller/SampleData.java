package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.HashMap;
import java.util.Map;

public class SampleData {
  private final VariantInfo owner;
  private final Map<Allele, AlleleCounter> alleleMap;

  public SampleData(final VariantInfo owner) {
    this.owner = owner;
    this.alleleMap = new HashMap<>();
  }

  public AlleleCounter computeAllele(Allele alt) {
    if (owner.getRefAllele().equals(alt, true)) {
      alt = owner.getRefAllele();
    }
    return alleleMap.computeIfAbsent(alt, k -> new AlleleCounter());
  }

  public Map<Allele, AlleleCounter> getAlleleMap() {
    return alleleMap;
  }

  @Override
  public String toString() {
    return "SampleData{" +
        "owner=" + owner +
        ", alleleMap=" + alleleMap +
        '}';
  }
}
