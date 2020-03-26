package com.epam.bioinf.variantcaller.cmdline;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;

import java.nio.file.Path;

import static java.io.File.pathSeparatorChar;

/**
 * Class parses arguments and creates ParsedArguments
 */
public class CommandLineParser {
  private final ParsedArguments parsedArguments;
  private final String FASTA_KEY = "fasta";
  private final String BED_KEY = "bed";
  private final String SAM_KEY = "sam";

  private CommandLineParser(String[] args) {
    OptionParser optionParser = new OptionParser() {
      {
        accepts(FASTA_KEY);
        accepts(BED_KEY);
        accepts(SAM_KEY);
      }
    };
    OptionSpec<Path> fasta = getOptionSpecByParameter(optionParser, FASTA_KEY);
    OptionSpec<Path> bed = getOptionSpecByParameter(optionParser, BED_KEY);
    OptionSpec<Path> sam = getOptionSpecByParameter(optionParser, SAM_KEY);
    OptionSet options = optionParser.parse(args);
    parsedArguments = new ParsedArguments(
        options.valuesOf(fasta),
        options.valuesOf(bed),
        options.valuesOf(sam)
    );
  }

  private OptionSpec<Path> getOptionSpecByParameter(OptionParser parser, String key) {
    return parser
        .accepts(key)
        .withRequiredArg()
        .required()
        .ofType(String.class)
        .withValuesConvertedBy(new PathConverter())
        .withValuesSeparatedBy(pathSeparatorChar);
  }

  /**
   * Method returns ParsedArguments instance
   */
  public static ParsedArguments parse(String[] args) {
    return new CommandLineParser(args).getParsedArguments();
  }

  private ParsedArguments getParsedArguments() {
    return parsedArguments;
  }
}
