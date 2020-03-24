package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.handlers.FastaHadler;
import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;

public class Main {
  public static void main(String[] args) {
    ParsedArguments parsedArguments = CommandLineParser.parse(args);
    FastaHadler fastaHadler = new FastaHadler(parsedArguments.getFastaPath());
    System.out.println("N of nucleotides " + fastaHadler.countNucleotides());
    System.out.println("GC content " + fastaHadler.getGcContent());
  }
}
