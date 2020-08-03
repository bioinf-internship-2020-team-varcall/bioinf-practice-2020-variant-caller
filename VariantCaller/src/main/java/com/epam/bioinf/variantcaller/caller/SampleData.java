package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds an allele map where we count how many different alleles
 * we saw at the position provided by an owner variant info
 */
public class SampleData {
  private final VariantInfo owner;
  private final Map<Allele, AlleleCounter> alleleMap;

  public SampleData(final VariantInfo owner) {
    this.owner = owner;
    this.alleleMap = new HashMap<>();
  }

  /**
   * Tries to get a counter by a given allele and if there is none then
   * puts one to the map
   *
   * @param alt - allele that we have seen at the position provided by an owner variant info
   * @return - counter by a given allele
   */
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
