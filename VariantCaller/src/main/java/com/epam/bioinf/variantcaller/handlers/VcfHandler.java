package com.epam.bioinf.variantcaller.handlers;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VcfHandler {
  private static final String OUTPUT_FILE_NAME = "output.vcf";

  public static void writeVcf(Optional<Path> outputDirectory, List<VariantContext> variants) {
    VariantContextWriter writer = configureVcfWriter(outputDirectory);
    VCFHeader header = getHeader(variants);
    writer.writeHeader(header);
    variants.forEach(writer::add);
    writer.close();
  }

  private static VCFHeader getHeader(List<VariantContext> variants) {
    final Set<String> sampleSet = new HashSet<>();
    variants.forEach(
        variant -> sampleSet.addAll(variant.getSampleNames()));

    final Set<VCFHeaderLine> metaData = new HashSet<>();
    metaData.add(VCFStandardHeaderLines.getInfoLine(VCFConstants.ALLELE_COUNT_KEY));
    metaData.add(VCFStandardHeaderLines.getInfoLine(VCFConstants.ALLELE_FREQUENCY_KEY));
    metaData.add(VCFStandardHeaderLines.getInfoLine(VCFConstants.ALLELE_NUMBER_KEY));
    metaData.add(VCFStandardHeaderLines.getFormatLine(VCFConstants.DEPTH_KEY));
    metaData.add(VCFStandardHeaderLines.getFormatLine(VCFConstants.GENOTYPE_KEY));
    metaData.add(new VCFFormatHeaderLine(
        "DPG",
        VCFHeaderLineCount.G, //one value of each genotype
        VCFHeaderLineType.Integer,
        "Depth for each allele"));
    metaData.add(new VCFInfoHeaderLine(
        "INDEL",
        1,
        VCFHeaderLineType.Flag,
        "Variant is indel"));
    metaData.add(new VCFFormatHeaderLine(
        "AVG-MAPQ",
        VCFHeaderLineCount.G,
        VCFHeaderLineType.Integer,
        "Average mapping quality"));
    metaData.add(new VCFFormatHeaderLine(
        "DP4",
        VCFHeaderLineCount.G,
        VCFHeaderLineType.Integer,
        "Number of 1) forward ref alleles; 2) reverse ref; 3) forward non-ref;" +
            " 4) reverse non-ref alleles, used in variant calling"));
    metaData.add(new VCFFormatHeaderLine(
        "PV-FISCHER",
        VCFHeaderLineCount.G,
        VCFHeaderLineType.Integer,
        "Fischer's exact test for strand bias"));
    metaData.add(new VCFFormatHeaderLine(
        "PV-TTEST-BASEQ",
        VCFHeaderLineCount.G,
        VCFHeaderLineType.Integer,
        "comparison of base quality values for two groups of reads - variant and reference"));
    metaData.add(new VCFFormatHeaderLine(
        "PV-TTEST-MAPQ",
        VCFHeaderLineCount.G,
        VCFHeaderLineType.Integer,
        "comparison of mapping quality values for two groups of reads - variant and reference"));

    return new VCFHeader(metaData, sampleSet);
  }

  private static VariantContextWriter configureVcfWriter(Optional<Path> outputDirectory) {
    OutputStream outputStream = System.out;

    if (outputDirectory.isPresent()) {
      File outputVcf = new File(String.valueOf(outputDirectory.get().resolve(OUTPUT_FILE_NAME)));
      try {
        outputVcf.createNewFile();
        outputStream = new FileOutputStream(outputVcf);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return new VariantContextWriterBuilder()
        .clearOptions()
        .setOutputVCFStream(outputStream).build();
  }
}
