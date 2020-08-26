package com.epam.bioinf.variantcaller.caller.variant;

import com.epam.bioinf.variantcaller.caller.sample.SampleData;
import com.epam.bioinf.variantcaller.exceptions.caller.NoGenotypesException;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the information about a particular genome base and its variants.
 */
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

  public Allele getRefAllele() {
    return refAllele;
  }

  /**
   * Gets a sample by a given sample name and if it is not found, creates one
   * and puts it to the map.
   *
   * @param sampleName - name of a computed sample
   * @return - sample data by a given sample name
   */
  public SampleData computeSample(String sampleName) {
    SampleData sampleData = sampleDataMap.get(sampleName);
    if (sampleData == null) {
      sampleData = new SampleData(this);
      sampleDataMap.put(sampleName, sampleData);
    }
    return sampleData;
  }

  /**
   * Transforms VariantInfo to VariantContext using VariantContextBuilderWrapper.
   *
   * @return - resulting VariantContext
   * @see VariantContext
   * @see VariantContextBuilderWrapper
   */
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
