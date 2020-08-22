package com.epam.bioinf.variantcaller.caller.variant;

import com.epam.bioinf.variantcaller.caller.sample.SampleData;
import com.epam.bioinf.variantcaller.caller.sample.SampleMetrics;
import com.epam.bioinf.variantcaller.exceptions.caller.NoGenotypesException;
import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a wrapper for VariantContextBuilder.
 *
 * @see VariantContextBuilder
 */
public class VariantContextBuilderWrapper {
  private final VariantContextBuilder variantContextBuilder;
  private final Allele refAllele;
  private static final int MIN_DEPTH = 1;
  private static final double MIN_FRACTION = 1.0 / 1000.0;

  public VariantContextBuilderWrapper(Allele refAllele) {
    variantContextBuilder = new VariantContextBuilder();
    this.refAllele = refAllele;
  }

  /**
   * @see VariantContextBuilder
   */
  public VariantContext make() {
    return variantContextBuilder.make();
  }

  /**
   * @see VariantContextBuilder
   */
  public VariantContextBuilderWrapper start(int pos) {
    variantContextBuilder.start(pos);
    return this;
  }

  /**
   * @see VariantContextBuilder
   */
  public VariantContextBuilderWrapper stop(int pos) {
    variantContextBuilder.stop(pos);
    return this;
  }

  /**
   * @see VariantContextBuilder
   */
  public VariantContextBuilderWrapper chr(String contig) {
    variantContextBuilder.chr(contig);
    return this;
  }

  /**
   * Counts all the alleles in all the genotypes and sets its total count
   * as an attribute.
   */
  public VariantContextBuilderWrapper countAndSetAlleleNumber() {
    List<Integer> alleleCnt = getAlleleCntList(variantContextBuilder.getAlleles(),
        variantContextBuilder.getGenotypes());
    int alleleNumber = alleleCnt.stream().mapToInt(val -> val).sum();
    variantContextBuilder.attribute(VCFConstants.ALLELE_NUMBER_KEY, alleleNumber);
    return this;
  }

  /**
   * Counts all the matching alleles in the different genotypes and sets its counts
   * and frequencies(which are basically counts of matching alleles
   * divided by total number of alleles) as attributes.
   */
  public VariantContextBuilderWrapper countAndSetAlleleFrequencies() {
    List<Integer> alleleCntList = getAlleleCntList(variantContextBuilder.getAlleles(),
        variantContextBuilder.getGenotypes());
    List<Double> alleleFrequencies = getAlleleFrequenciesList(alleleCntList,
        (int) variantContextBuilder.getAttributes().get(VCFConstants.ALLELE_NUMBER_KEY));
    if (!alleleCntList.isEmpty()) {
      variantContextBuilder.attribute(VCFConstants.ALLELE_COUNT_KEY, alleleCntList);
      variantContextBuilder.attribute(VCFConstants.ALLELE_FREQUENCY_KEY, alleleFrequencies);
    }
    return this;
  }

  /**
   * Gets map with sample data and creates genotypes out of its values.
   * Then it sets all the genotypes and genotype's alleles as builder attributes.
   * <p>
   * Also there is a check whether any allele is indel. If that is true then
   * computed variant context is considered to be INDEL or MIXED.
   */
  public VariantContextBuilderWrapper genotypesAndAlleles(Map<String, SampleData> sampleDataMap) {
    boolean indel = refAllele.getBaseString().length() != 1;
    List<Genotype> genotypes = new ArrayList<>();
    Set<Allele> alleles = new TreeSet<>();
    for (Map.Entry<String, SampleData> entry : sampleDataMap.entrySet()) {
      if (!indel) {
        if (checkIfAnyAlleleIsIndel(entry.getValue())) {
          indel = true;
        }
      }
      Genotype genotype = getGenotypeForSample(entry.getKey(), sampleDataMap);
      if (genotype != null) {
        genotypes.add(genotype);
        alleles.addAll(genotype.getAlleles());
      }
    }
    if (genotypes.isEmpty()) throw new NoGenotypesException("There are no genotypes");
    alleles.add(refAllele);
    if (indel) variantContextBuilder.attribute("INDEL", Boolean.TRUE);
    variantContextBuilder.genotypes(genotypes);
    variantContextBuilder.alleles(alleles);
    return this;
  }

  private Genotype getGenotypeForSample(String sampleName, Map<String, SampleData> sampleDataMap) {
    SampleData sampleData = sampleDataMap.get(sampleName);
    SampleMetrics sampleMetrics = new SampleMetrics(sampleData.getAlleleMap());
    ArrayList<Allele> sampleAlleles = new ArrayList<>();
    ArrayList<Integer> sampleDepths = new ArrayList<>();
    int dp = sampleMetrics.getDp();
    if (dp >= MIN_DEPTH) {
      for (Map.Entry<Allele, Integer> entry : sampleMetrics.getAlleleCnt().entrySet()) {
        if ((float) entry.getValue() / (float) dp < MIN_FRACTION) {
          continue;
        }
        if (entry.getKey().isReference()) {
          sampleAlleles.add(0, entry.getKey());
          sampleDepths.add(0, entry.getValue());
        } else {
          sampleAlleles.add(entry.getKey());
          sampleDepths.add(entry.getValue());
        }
      }
      if (!sampleAlleles.isEmpty()) {
        return new GenotypeBuilder(sampleName, sampleAlleles)
            .DP(dp)
            .attribute("DPG", sampleDepths)
            .attribute("DP4", sampleMetrics.getDp4())
            .attribute("AVG-MAPQ", sampleMetrics.getAverageMappingQuality())
            .attribute("PV-FISCHER", sampleMetrics.getFischerExactTestP())
            .attribute("PV-TTEST-BASEQ", sampleMetrics.getBaseQsTtestP())
            .attribute("PV-TTEST-MAPQ", sampleMetrics.getMapQsTtestP())
            .make();
      }
    }
    return null;
  }

  private List<Integer> getAlleleCntList(List<Allele> alleles, List<Genotype> genotypes) {
    List<Integer> alleleCnt = new ArrayList<>();
    for (final Allele alt : alleles) {
      alleleCnt.add(genotypes.stream()
          .mapToInt(genotype -> {
            List<Allele> genotypeAlleles = genotype.getAlleles();
            //DPG is always able to be casted to a list of integer
            @SuppressWarnings("unchecked")
            List<Integer> dpg = (List<Integer>) genotype.getAnyAttribute("DPG");
            for (int i = 0; i < genotypeAlleles.size(); i++) {
              if (genotypeAlleles.get(i).equals(alt)) {
                return dpg.get(i);
              }
            }
            return 0;
          })
          .sum());
    }
    return alleleCnt;
  }

  private List<Double> getAlleleFrequenciesList(List<Integer> alleleCnt, int alleleNumber) {
    return alleleCnt.stream()
        .map(cnt -> new BigDecimal(cnt / (double) alleleNumber)
            .setScale(3, RoundingMode.HALF_UP).doubleValue())
        .collect(Collectors.toList());
  }

  private boolean checkIfAnyAlleleIsIndel(SampleData singleSample) {
    return singleSample.getAlleleMap().keySet()
        .stream()
        .anyMatch(allele -> allele.getBaseString().length() != 1);
  }
}
