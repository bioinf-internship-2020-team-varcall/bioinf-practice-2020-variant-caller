package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.exceptions.caller.NoGenotypesException;
import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.math3.distribution.TDistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    HashMap<Allele, Integer> alleleCnt = new HashMap<>();
    ArrayList<Allele> sampleAlleles = new ArrayList<>();
    Integer[] dp4 = new Integer[]{0, 0, 0, 0};
    List<Integer> refBaseQs = new ArrayList<>();
    List<Integer> altBaseQs = new ArrayList<>();
    List<Integer> refMapQs = new ArrayList<>();
    List<Integer> altMapQs = new ArrayList<>();
    for (Allele allele : sampleData.getAlleleMap().keySet()) {
      if (allele.isNonReference() && allele.getDisplayString().equals("N")) continue;
      AlleleCounter alleleCounter = sampleData.getAlleleMap().get(allele);
      if (allele.isReference()) {
        dp4[0] += alleleCounter.getForwardStrandCount();
        dp4[1] += alleleCounter.getReversedStrandCount();
        refBaseQs.addAll(alleleCounter.getBaseQs());
        refMapQs.addAll(alleleCounter.getMapQs());
      } else {
        dp4[2] += alleleCounter.getForwardStrandCount();
        dp4[3] += alleleCounter.getReversedStrandCount();
        altBaseQs.addAll(alleleCounter.getBaseQs());
        altMapQs.addAll(alleleCounter.getMapQs());
      }
      alleleCnt.put(allele, alleleCounter.count());
    }
    ArrayList<Integer> sampleDepths = new ArrayList<>();
    int dp = alleleCnt.keySet()
        .stream()
        .map(alleleCnt::get)
        .mapToInt(Integer::intValue).sum();
    if (dp >= MIN_DEPTH) {
      for (Map.Entry<Allele, Integer> entry : alleleCnt.entrySet()) {
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
            .attribute("DP4", Arrays.asList(dp4))
            .attribute("AVG-MAPQ", getAverageValue(
                Stream.concat(refMapQs.stream(), altMapQs.stream())
                    .collect(Collectors.toList())
                )
            )
            .attribute("PV-FISCHER", getPValueFischer(dp4))
            .attribute("PV-TTEST-BASEQ", getPValueTTest(refBaseQs, altBaseQs))
            .attribute("PV-TTEST-MAPQ", getPValueTTest(refMapQs, altMapQs))
            .make();
      }
    }
    return null;
  }

  public double getPValueTTest(List<Integer> refQs, List<Integer> altQs) {
    if (refQs.size() == 0 || altQs.size() == 0) {
      return 0.0;
    }
    double refAverage = getAverageValue(refQs);
    double altAverage = getAverageValue(altQs);
    double mRef = getSd(refQs, refAverage) / Math.sqrt(refQs.size());
    double mAlt = getSd(altQs, altAverage) / Math.sqrt(altQs.size());
    double tValue = (refAverage - altAverage) / Math.sqrt(Math.pow(mRef, 2)
        + Math.pow(mAlt, 2));
    int df = refQs.size() + altQs.size() - 1;
    TDistribution tDistribution = new TDistribution(df);
    try {
      return BigDecimal
          .valueOf(tDistribution.cumulativeProbability(tValue))
          .setScale(3, RoundingMode.HALF_UP)
          .doubleValue();
    } catch (NumberFormatException ex) {
      return 0.0;
    }
  }

  public int getAverageValue(List<Integer> list) {
    return list.stream().mapToInt(Integer::intValue).sum() / list.size();
  }

  public double getSd(List<Integer> list, double average) {
    double sqrDevSum = list.stream().map(val -> Math.pow(val - average, 2)).mapToDouble(Double::doubleValue).sum();
    return Math.sqrt(sqrDevSum / list.size());
  }

  public double getPValueFischer(Integer[] dp4) {
    List<List<Integer>> list = new ArrayList<>();
    int r1 = dp4[0] + dp4[1];
    int r2 = dp4[2] + dp4[3];
    int c1 = dp4[0] + dp4[2];
    int c2 = dp4[1] + dp4[3];
    for (int a = 0, b = r1; b >= 0 && a <= r1; a++, b--) {
      for (int c = 0, d = r2; d >= 0 && c <= r2; c++, d--) {
        if (a + b == r1 && c + d == r2 && a + c == c1 && b + d == c2) {
          list.add(new ArrayList<>(Arrays.asList(a, b, c, d)));
        }
      }
    }
    double originalPv = getPv(Arrays.asList(dp4[0], dp4[1], dp4[2], dp4[3]));
    return BigDecimal.valueOf(list.stream()
        .filter(arr -> getPv(arr) <= originalPv)
        .mapToDouble(VariantContextBuilderWrapper::getPv)
        .sum())
        .setScale(3, RoundingMode.HALF_UP).doubleValue();
  }

  public static double getPv(List<Integer> vals) {
    int r1 = vals.get(0) + vals.get(1);
    int r2 = vals.get(2) + vals.get(3);
    int c1 = vals.get(0) + vals.get(2);
    int c2 = vals.get(1) + vals.get(3);
    return (fact(r1) * fact(r2) * fact(c1) * fact(c2)) / (fact(r1 + r2)
        * fact(vals.get(0)) * fact(vals.get(1)) * fact(vals.get(2)) * fact(vals.get(3)));
  }

  public static double fact(int number) {
    long result = 1;
    for (int factor = 2; factor <= number; factor++) {
      result *= factor;
    }
    return result;
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
