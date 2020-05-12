package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.Allele;

import java.util.ArrayList;

public class Variant {

  String contig;
  int pos;
  ArrayList<Allele> variants;
  Allele refAllele;

  public Variant(String contig, int pos, ArrayList<Allele> variants, Allele refAllele) {
    this.contig = contig;
    this.pos = pos;
    this.variants = variants;
    this.refAllele = refAllele;
  }

  public int getPos() {
    return pos;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

  public ArrayList<Allele> getVariants() {
    return variants;
  }

  public void setVariants(ArrayList<Allele> variants) {
    this.variants = variants;
  }

  public Allele getRefAllele() {
    return refAllele;
  }

  public void setRefAllele(Allele refAllele) {
    this.refAllele = refAllele;
  }

  @Override
  public String toString() {
    return "Variant{" +
        "contig='" + contig + '\'' +
        ", pos=" + pos +
        ", variants=" + variants +
        ", refAllele=" + refAllele +
        '}';
  }
}
