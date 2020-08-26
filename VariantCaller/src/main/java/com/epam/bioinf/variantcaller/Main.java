package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import com.epam.bioinf.variantcaller.handlers.VcfHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.VariantContext;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Class launches program with command line arguments(implementation is
 *  temporary and will be updated in future versions,
 * now it is used for exception check in integration tests)
 */
public class Main {
  public static void main(String[] args) {
    ParsedArguments parsedArguments = CommandLineParser.parse(args);
    System.err.println("Reading data...");
    IndexedFastaSequenceFile fastaSequenceFile =
        new FastaHandler(parsedArguments).getFastaSequenceFile();
    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
    List<VariantContext> variants = new Caller(fastaSequenceFile, samRecords).findVariants();
    VcfHandler.writeVcf(parsedArguments.getOutputDirectory(), variants);
    System.err.println("Success");
  }
}
