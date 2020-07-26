package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class VariantInfo {
  private final Allele refAllele;
  private final String contig;
  private final int pos;
  private static final int MIN_DEPTH = 1;
  private static final double MIN_FRACTION = 1.0 / 1000.0;
  private final Map<String, SampleData> sampleData;

  VariantInfo(String contig, int pos, Allele refAllele) {
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

  public VariantContext makeContext() {
    boolean indel = refAllele.getBaseString().length() != 1;
    VariantContextBuilder builder = new VariantContextBuilder();
    builder.start(pos);
    builder.chr(contig);
    List<Genotype> genotypes = new ArrayList<>();
    Set<Allele> alleles = new TreeSet<>();
    for (Map.Entry<String, SampleData> entry : sampleData.entrySet()) {
      indel = checkIfAnyAlleleIsIndel(entry.getValue());
      Genotype genotype = getGenotypeForSample(entry.getKey());
      if (genotype != null) {
        genotypes.add(genotype);
        alleles.addAll(genotype.getAlleles());
      }
    }
    if (genotypes.isEmpty()) return null;
    alleles.add(refAllele);
    if (indel) builder.attribute("INDEL", Boolean.TRUE);
    builder.genotypes(genotypes);
    builder.alleles(alleles);
    List<Integer> alleleCnt = getAlleleCntList(alleles, genotypes);
    int alleleNumber = alleleCnt.stream().mapToInt(val -> val).sum();
    builder.attribute(VCFConstants.ALLELE_NUMBER_KEY, alleleNumber);
    List<Double> alleleFrequencies = getAlleleFrequenciesList(alleleCnt, alleleNumber);
    if (!alleleCnt.isEmpty()) {
      builder.attribute(VCFConstants.ALLELE_COUNT_KEY, alleleCnt);
      builder.attribute(VCFConstants.ALLELE_FREQUENCY_KEY, alleleFrequencies);
    }
    builder.stop(pos + refAllele.getBaseString().length() - 1);
    VariantContext context = builder.make();
    if (context.getAlternateAlleles().isEmpty()) return null;
    return context;
  }

  public Genotype getGenotypeForSample(String sampleName) {
    SampleData sd = sampleData.get(sampleName);
    HashMap<Allele, Integer> alleleCnt = new HashMap<>();
    ArrayList<Allele> sampleAlleles = new ArrayList<>();
    for (Allele allele : sd.alleleMap.keySet()) {
      if (allele.isNonReference() && allele.getDisplayString().equals("N")) continue;
      AlleleData ad = sd.alleleMap.get(allele);
      alleleCnt.put(allele, ad.count());
    }
    ArrayList<Integer> sampleDepths = new ArrayList<>();
    int totalSampleDepth = alleleCnt.keySet()
        .stream()
        .map(alleleCnt::get)
        .mapToInt(Integer::intValue).sum();
    if (totalSampleDepth > MIN_DEPTH) {
      for (Map.Entry<Allele, Integer> entry : alleleCnt.entrySet()) {
        if ((float) entry.getValue() / (float) totalSampleDepth < MIN_FRACTION) {
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
        final GenotypeBuilder gb = new GenotypeBuilder(sampleName, sampleAlleles);
        gb.DP(totalSampleDepth);
        gb.attribute("DPG", sampleDepths);
        return gb.make();
      }
    }
    return null;
  }

  public List<Integer> getAlleleCntList(Set<Allele> alleles, List<Genotype> genotypes) {
    List<Integer> alleleCnt = new ArrayList<>();
    for (final Allele alt : alleles) {
      alleleCnt.add(genotypes.stream()
          .mapToInt(genotype -> {
            List<Allele> al = genotype.getAlleles();
            @SuppressWarnings("unchecked") //DPG is always able to be casted to a list of integer
                List<Integer> dpg = (List<Integer>) genotype.getAnyAttribute("DPG");
            for (int i = 0; i < genotype.getAlleles().size(); i++) {
              if (al.get(i).equals(alt)) {
                return dpg.get(i);
              }
            }
            return 0;
          })
          .sum());
    }
    return alleleCnt;
  }

  public List<Double> getAlleleFrequenciesList(List<Integer> alleleCnt, int alleleNumber) {
    return alleleCnt.stream()
        .map(cnt -> new BigDecimal(cnt / (double) alleleNumber)
            .setScale(3, RoundingMode.HALF_UP).doubleValue())
        .collect(Collectors.toList());
  }

  public boolean checkIfAnyAlleleIsIndel(SampleData singleSample) {
    return singleSample.alleleMap.keySet()
        .stream()
        .anyMatch(allele -> allele.getBaseString().length() != 1);
  }
}
