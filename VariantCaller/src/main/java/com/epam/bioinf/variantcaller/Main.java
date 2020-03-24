package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;

public class Main {
  public static void main(String[] args) {
    ParsedArguments parsedArguments = CommandLineParser.parse(args);

    System.out.println("Parsed fasta path = " + parsedArguments.getFastaPath());
    System.out.println("Parsed bed paths = " + parsedArguments.getBedPaths());
    System.out.println("Parsed sam paths = " + parsedArguments.getSamPaths());
  }
}
