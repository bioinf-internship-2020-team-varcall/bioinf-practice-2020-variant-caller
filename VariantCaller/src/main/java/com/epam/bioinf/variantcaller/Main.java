package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;

/**
 * Class launches program with command line arguments(implementation is
 * is temporary and will be updated in future versions,
 * now it is used for exception check in integration tests)
 */
public class Main {
  public static void main(String[] args) {
    CommandLineParser.parse(args);
  }
}
