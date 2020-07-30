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
  private final Map<String, SampleData> sampleDataMap;

  public VariantInfo(String contig, int pos, Allele refAllele) {
    this.refAllele = refAllele;
    this.contig = contig;
    this.pos = pos;
    sampleDataMap = new HashMap<>();
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

  public SampleData computeSample(String sampleName) {
    SampleData sampleData = sampleDataMap.get(sampleName);
    if (sampleData == null) {
      sampleData = new SampleData(this);
      sampleDataMap.put(sampleName, sampleData);
    }
    return sampleData;
  }

  public VariantContext makeVariantContext() {
    VariantContextBuilderWrapper builder = new VariantContextBuilderWrapper(refAllele);
    try {
      VariantContext context = builder
          .start(pos)
          .stop(pos + refAllele.getBaseString().length() - 1)
          .chr(contig)
          .genotypesAndAlleles(sampleDataMap)
          .countAndSetAlleleNumber()
          .countAndSetAlleleFrequencies()
          .make();
      return context.getAlternateAlleles().isEmpty() ? null : context;
    } catch (NoGenotypesException ex) {
      return null;
    }
  }
}
