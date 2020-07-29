package com.epam.bioinf.variantcaller.handlers;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class VcfHandler {
  private static final String OUTPUT_FILE_NAME = "output.vcf";

  public VcfHandler(Optional<Path> outputFilePath, List<VariantContext> variants) {
    VariantContextWriter writer = configureVcfWriter(outputFilePath);
    VCFHeader header = getHeader(variants);
    writer.writeHeader(header);
    List<VariantContext> variantContexts = getVariantContexts(variants);
    variantContexts.forEach(writer::add);
    writer.close();
  }

  private List<VariantContext> getVariantContexts(List<VariantContext> variants) {
    List<VariantContext> variantContexts = new ArrayList<>();
    variants.forEach(variant -> {
      var builder = new VariantContextBuilder();
      var contig = variant.getContig();
      var alleles = variant.getAlleles();
      var pos = variant.getStart();
      var end = variant.getEnd();
      var info = variant.getCommonInfo().getAttributes();
      var genotypes = variant.getGenotypes();
      builder.alleles(alleles).start(pos).stop(end).chr(contig).attributes(info).genotypes(genotypes);
      variantContexts.add(builder.make());
    });

    return variantContexts;
  }

  private VCFHeader getHeader(List<VariantContext> variants) {

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
        VCFHeaderLineCount.G,//one value of each genotype
        VCFHeaderLineType.Integer,
        "Depth for each allele"));
    metaData.add(new VCFInfoHeaderLine(
        "INDEL",
        1,
        VCFHeaderLineType.Flag,
        "Variant is indel"));


    return new VCFHeader(metaData, sampleSet);
  }

  private VariantContextWriter configureVcfWriter(Optional<Path> outputFilePath) {
    File outputFile = outputFilePath.map(path ->
        new File(String.valueOf(path.resolve(OUTPUT_FILE_NAME))))
        .orElse(null);
    return new VariantContextWriterBuilder()
        .clearOptions()
        .setOutputFile(outputFile).build();
  }
}
