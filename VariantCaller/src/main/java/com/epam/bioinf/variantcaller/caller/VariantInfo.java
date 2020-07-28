package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.exceptions.caller.NoGenotypesException;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.HashMap;
import java.util.Map;

public class VariantInfo {
  private final Allele refAllele;
  private final String contig;
  private final int pos;
  private final Map<String, SampleData> sampleData;

  public VariantInfo(String contig, int pos, Allele refAllele) {
    this.refAllele = refAllele;
    this.contig = contig;
    this.pos = pos;
    sampleData = new HashMap<>();
  }

  public int getPos() {
    return pos;
  }

  public String getContig() {
    return contig;
  }

  public Allele getRefAllele() {
    return refAllele;
  }

  public SampleData getSample(String sampleName) {
    SampleData sd = sampleData.get(sampleName);
    if (sd == null) {
      sd = new SampleData(this);
      this.sampleData.put(sampleName, sd);
    }
    return sd;
  }

  public VariantContext makeVariantContext() {
    VariantContextBuilderWrapper builder = new VariantContextBuilderWrapper(refAllele);
    try {
      VariantContext context = builder
          .start(pos)
          .stop(pos + refAllele.getBaseString().length() - 1)
          .chr(contig)
          .genotypesAndAlleles(sampleData)
          .countAndSetAlleleNumber()
          .countAndSetAlleleFrequencies()
          .make();
      if (context.getAlternateAlleles().isEmpty()) return null;
      return context;
    } catch (NoGenotypesException ex) {
      return null;
    }
  }
}
