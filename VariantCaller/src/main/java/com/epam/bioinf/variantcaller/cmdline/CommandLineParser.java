package com.epam.bioinf.variantcaller.cmdline;

import com.epam.bioinf.variantcaller.exceptions.parser.region.RegionBothIntervalOptionsException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;

import java.nio.file.Path;
import java.util.Optional;

import static java.io.File.pathSeparatorChar;

/**
 * Class parses arguments and creates ParsedArguments
 */
public class CommandLineParser {
  private final ParsedArguments parsedArguments;
  private static final String FASTA_KEY = "fasta";
  private static final String BED_KEY = "bed";
  private static final String SAM_KEY = "sam";
  private static final String REGION_KEY = "region";

  private CommandLineParser(String[] args) {
    OptionParser optionParser = new OptionParser() {
      {
        accepts(FASTA_KEY);
        accepts(BED_KEY);
        accepts(SAM_KEY);
        accepts(REGION_KEY);
      }
    };
    OptionSpec<Path> fasta = getOptionSpecPathsByParameter(optionParser, FASTA_KEY);
    OptionSpec<Path> bed = getOptionalOptionSpecPathsByParameter(optionParser, BED_KEY);
    OptionSpec<Path> sam = getOptionSpecPathsByParameter(optionParser, SAM_KEY);
    OptionSpec<String> region = getOptionSpecStringByParameter(optionParser, REGION_KEY);
    OptionSet options = optionParser.parse(args);
    if (options.hasArgument(bed) && options.hasArgument(region)) {
      throw new RegionBothIntervalOptionsException();
    }
    parsedArguments = new ParsedArguments(
        options.valuesOf(fasta),
        options.valuesOf(bed),
        options.valuesOf(sam),
        Optional.ofNullable(options.valueOf(region))
    );
  }

  private OptionSpec<Path> getOptionSpecPathsByParameter(OptionParser parser, String key) {
    return parser
        .accepts(key)
        .withRequiredArg()
        .required()
        .ofType(String.class)
        .withValuesConvertedBy(new PathConverter())
        .withValuesSeparatedBy(pathSeparatorChar);
  }

  private OptionSpec<Path> getOptionalOptionSpecPathsByParameter(OptionParser parser, String key) {
    return parser
        .accepts(key)
        .withRequiredArg()
        .ofType(String.class)
        .withValuesConvertedBy(new PathConverter())
        .withValuesSeparatedBy(pathSeparatorChar);
  }

  private OptionSpec<String> getOptionSpecStringByParameter(OptionParser parser, String key) {
    return parser
        .accepts(key)
        .withRequiredArg();
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
