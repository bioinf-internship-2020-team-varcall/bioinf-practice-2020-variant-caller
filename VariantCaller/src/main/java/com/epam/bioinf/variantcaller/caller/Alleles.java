package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

/**
 * Holds ref and alt alleles.
 */
public class Alleles {
  private final Allele refAllele;
  private final Allele altAllele;

  public Alleles(Allele refAllele, Allele altAllele) {
    this.refAllele = refAllele;
    this.altAllele = altAllele;
  }

  public Allele getRefAllele() {
    return refAllele;
  }

  public Allele getAltAllele() {
    return altAllele;
  }
}
