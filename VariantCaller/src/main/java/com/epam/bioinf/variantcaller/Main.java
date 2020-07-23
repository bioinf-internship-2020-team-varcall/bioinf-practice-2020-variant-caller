package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.caller.Caller;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.FastaHandler;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.util.List;

/**
 * Class launches program with command line arguments(implementation is
 *  temporary and will be updated in future versions,
 * now it is used for exception check in integration tests)
 */
public class Main {
  public static void main(String[] args) {
    ParsedArguments parsedArguments = CommandLineParser.parse(args);
    System.out.println("Reading data...");
    IndexedFastaSequenceFile fastaSequenceFile =
        new FastaHandler(parsedArguments).getFastaSequenceFile();
    List<SAMRecord> samRecords = new SamHandler(parsedArguments).getSamRecords();
    new Caller(fastaSequenceFile, samRecords).findVariants();
    System.out.println("Success");
  }
}
