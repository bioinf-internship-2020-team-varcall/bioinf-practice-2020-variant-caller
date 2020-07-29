package com.epam.bioinf.variantcaller.handlers;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VcfHandler {
  private static final String OUTPUT_FILE_NAME = "output.vcf";

  public VcfHandler(Optional<Path> outputFilePath, List<VariantContext> variants) {
    VariantContextWriter writer = configureVcfWriter(outputFilePath);
    VCFHeader header = getHeader();
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
      builder.alleles(alleles).start(pos).stop(end).chr(contig).unfiltered().noID();
      variantContexts.add(builder.make());
    });

    return variantContexts;
  }

  private VCFHeader getHeader() {
    VCFHeader header = new VCFHeader();
    VCFInfoHeaderLine metaData = new VCFInfoHeaderLine(
        "COVID-19",
        VCFHeaderLineCount.UNBOUNDED,
        VCFHeaderLineType.String,
        "ULTIMATE VCF POWER."
    );
    header.addMetaDataLine(metaData);
    return header;
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
