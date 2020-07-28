package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.HashMap;
import java.util.Map;

public class SampleData {
  final VariantInfo owner;
  @SuppressWarnings("unused")
  final Map<Allele, AlleleCounter> alleleMap;

  public SampleData(final VariantInfo owner) {
    this.owner = owner;
    this.alleleMap = new HashMap<>();
  }

  public AlleleCounter getAllele(Allele alt) {
    if (owner.getRefAllele().equals(alt, true)) {
      alt = owner.getRefAllele();
    }
    AlleleCounter ad = this.alleleMap.get(alt);
    if (ad == null) {
      ad = new AlleleCounter();
      this.alleleMap.put(alt, ad);
    }
    return ad;
  }


  @Override
  public String toString() {
    return "SampleData{" +
        "owner=" + owner +
        ", alleleMap=" + alleleMap +
        '}';
  }
}
