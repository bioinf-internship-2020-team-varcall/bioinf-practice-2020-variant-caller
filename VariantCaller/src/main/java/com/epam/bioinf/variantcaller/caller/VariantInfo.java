package com.epam.bioinf.variantcaller.caller;

import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFConstants;

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
    for (String sampleName : sampleData.keySet()) {
      indel = checkIfAnyAlleleIsIndel(sampleData.get(sampleName));
      Genotype genotype = getGenotypeForSample(sampleName);
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
    int alleleNumber = genotypes.stream().mapToInt(genotype -> genotype.getAlleles().size()).sum();
    builder.attribute(VCFConstants.ALLELE_NUMBER_KEY, alleleNumber);
    List<Integer> alleleCnt = getAlleleCntList(alleles, genotypes);
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
    int totalSampleDepth = alleleCnt.keySet().stream().map(alleleCnt::get).mapToInt(Integer::intValue).sum();
    if (totalSampleDepth > MIN_DEPTH) {
      for (Allele allele : alleleCnt.keySet()) {
        if ((float) alleleCnt.get(allele) / (float) totalSampleDepth < MIN_FRACTION) {
          continue;
        }
        sampleAlleles.add(allele);
        sampleDepths.add(alleleCnt.get(allele));
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
      if (alt.isReference()) continue;
      alleleCnt.add((int) genotypes.stream()
          .flatMap(genotype -> genotype.getAlleles().stream())
          .filter(a -> a.equals(alt))
          .count());
    }
    return alleleCnt;
  }

  public List<Double> getAlleleFrequenciesList(List<Integer> alleleCnt, int alleleNumber) {
    return alleleCnt.stream().
        map(cnt -> cnt / (double) alleleNumber).
        collect(Collectors.toList());
  }

  public boolean checkIfAnyAlleleIsIndel(SampleData singleSample) {
    return singleSample.alleleMap.keySet().stream().anyMatch(allele -> allele.getBaseString().length() != 1);
  }
}
