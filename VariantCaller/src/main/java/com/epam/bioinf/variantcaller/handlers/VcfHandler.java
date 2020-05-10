package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.helpers.TestHelper;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VcfHandler {
  List<VariantContext> variantContexts;

  public VcfHandler(ParsedArguments parsedArguments /*,PotentialVariants pv*/) {
    VariantContextWriter writer = getWriter();
    VCFHeader header = getHeader();
    writer.writeHeader(header);
    variantContexts = getVariantContexts();
    variantContexts.forEach(writer::add);
    writer.close();
  }

  private List<VariantContext> getVariantContexts(/*PotentialVariants pv*/) {
    List<VariantContext> variantContexts = new ArrayList<>();
    /*
    var variantContexts = new ArrayList<>();
    potentialVariants.forEach(variant-> {
        var buidler = new VariantContextBuilder();
        var refChar = variant.getRefChar();
        var alleles = variant.getAlleles();
        var contig = variant.getContig();
        var index = variant.getIndex();
        builder.refChar(source).chr(contig).alleles(alleles).index(index);
        variantContexts.add(builder.make());
    })
    */
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

  private VariantContextWriter getWriter() {
    File outputFile = new File(TestHelper.testFilePath("output.vcf"));
    return new VariantContextWriterBuilder()
        .clearOptions()
        .setOutputFile(outputFile).build();
  }
}
