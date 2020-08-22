package com.epam.bioinf.variantcaller.caller.sample;

import com.epam.bioinf.variantcaller.caller.position.PositionStatistics;
import htsjdk.variant.variantcontext.Allele;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Computes metrics of the variants
 */
public class SampleMetrics {

  private final Integer[] dp4;
  private final List<Integer> refBaseQs;
  private final List<Integer> altBaseQs;
  private final List<Integer> refMapQs;
  private final List<Integer> altMapQs;
  private final HashMap<Allele, Integer> alleleCnt;
  private final int dp;


  public SampleMetrics(Map<Allele, PositionStatistics> alleleMap) {
    this.dp4 = new Integer[]{0, 0, 0, 0};
    this.refBaseQs = new ArrayList<>();
    this.altBaseQs = new ArrayList<>();
    this.refMapQs = new ArrayList<>();
    this.altMapQs = new ArrayList<>();
    this.alleleCnt = new HashMap<>();

    for (Map.Entry<Allele, PositionStatistics> entry : alleleMap.entrySet()) {
      Allele allele = entry.getKey();
      if (allele.isNonReference() && allele.getDisplayString().equals("N")) continue;
      PositionStatistics positionStatistics = entry.getValue();
      if (allele.isReference()) {
        dp4[0] += positionStatistics.getForwardStrandCount();
        dp4[1] += positionStatistics.getReversedStrandCount();
        refBaseQs.addAll(positionStatistics.getBaseQs());
        refMapQs.addAll(positionStatistics.getMapQs());
      } else {
        dp4[2] += positionStatistics.getForwardStrandCount();
        dp4[3] += positionStatistics.getReversedStrandCount();
        altBaseQs.addAll(positionStatistics.getBaseQs());
        altMapQs.addAll(positionStatistics.getMapQs());
      }
      alleleCnt.put(allele, positionStatistics.count());
    }

    dp = alleleCnt.keySet()
        .stream()
        .map(alleleCnt::get)
        .mapToInt(Integer::intValue).sum();
  }

  /**
   * Returns p-value for baseQ bias t-test
   */
  public double getBaseQsTtestP() {
    return getPvalueTtest(refBaseQs, altBaseQs);
  }

  /**
   * Returns p-value for mapQ bias t-test
   */
  public double getMapQsTtestP() {
    return getPvalueTtest(refMapQs, altMapQs);
  }

  private double getPvalueTtest(List<Integer> refQs, List<Integer> altQs) {
    if (refQs.size() == 0 || altQs.size() == 0) {
      return Double.NaN;
    }
    double refAverage = getAverageValue(refQs);
    double altAverage = getAverageValue(altQs);
    double mRef = new StandardDeviation().evaluate(convertIntListToDoubleArray(refQs))
        / Math.sqrt(refQs.size());
    double mAlt = new StandardDeviation().evaluate(convertIntListToDoubleArray(altQs))
        / Math.sqrt(altQs.size());
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
      return Double.NaN;
    }
  }

  /**
   * Returns average mapping quality for the current position
   * among all the reads of the sample
   */
  public double getAverageMappingQuality() {
    return getAverageValue(
        Stream.concat(refMapQs.stream(), altMapQs.stream())
            .collect(Collectors.toList())
    );
  }

  /**
   * Returns p-value for Fischer's exact test
   */
  public double getFischerExactTestP() {
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
    double originalPv = getSinglePv(Arrays.asList(dp4[0], dp4[1], dp4[2], dp4[3]));
    try {
      return BigDecimal.valueOf(list.stream()
          .filter(arr -> getSinglePv(arr) <= originalPv)
          .mapToDouble(this::getSinglePv)
          .sum())
          .setScale(3, RoundingMode.HALF_UP).doubleValue();
    } catch (NumberFormatException ex) {
      return Double.NaN;
    }
  }

  /**
   * Returns the number of reads covering or bridging POS
   */
  public int getDp() {
    return dp;
  }

  /**
   * Number of
   * 1) forward ref alleles;
   * 2) reverse ref;
   * 3) forward non-ref;
   * 4) reverse non-ref alleles, used in variant calling
   */
  public List<Integer> getDp4() {
    return Arrays.asList(dp4);
  }

  /**
   * Returns the map where an allele matches its count
   */
  public HashMap<Allele, Integer> getAlleleCnt() {
    return alleleCnt;
  }

  private double getSinglePv(List<Integer> vals) {
    int r1 = vals.get(0) + vals.get(1);
    int r2 = vals.get(2) + vals.get(3);
    int c1 = vals.get(0) + vals.get(2);
    int c2 = vals.get(1) + vals.get(3);
    return (fact(r1) * fact(r2) * fact(c1) * fact(c2)) / (fact(r1 + r2)
        * fact(vals.get(0)) * fact(vals.get(1)) * fact(vals.get(2)) * fact(vals.get(3)));
  }

  private double fact(int number) {
    long result = 1;
    for (int factor = 2; factor <= number; factor++) {
      result *= factor;
    }
    return result;
  }

  private int getAverageValue(List<Integer> list) {
    return list.stream().collect(Collectors.averagingInt(Integer::intValue)).intValue();
  }

  private double[] convertIntListToDoubleArray(List<Integer> list) {
    return list.stream().mapToDouble(i -> i).toArray();
  }
}
