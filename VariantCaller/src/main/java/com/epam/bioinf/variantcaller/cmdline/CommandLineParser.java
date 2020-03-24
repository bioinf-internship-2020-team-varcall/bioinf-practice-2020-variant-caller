package com.epam.bioinf.variantcaller.cmdline;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;

import java.nio.file.Path;

import static java.io.File.pathSeparatorChar;

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

  public static ParsedArguments parse(String[] args) {
    return new CommandLineParser(args).getParsedArguments();
  }

  private ParsedArguments getParsedArguments() {
    return parsedArguments;
  }

  public static final class CommandLineMessages {
    private CommandLineMessages() {
      // restrict instantiation
    }

    public static final String FASTA_ARGS_COUNT_EXC =
        "Multiple or no paths to '.fasta' files were presented, must be 1";
    public static final String BED_ARGS_COUNT_EXC = "" +
        "No paths to '.bed' files were presented, must be 1 or more";
    public static final String SAM_ARGS_COUNT_EXC =
        "No paths to '.sam' files were presented, must be 1 or more";

    public static final String FASTA_EXTENSION_EXC =
        "'--fasta' parameters must contain a path to a file with '.fasta' extension";
    public static final String BED_EXTENSION_EXC =
        "'--bed' parameters must contain a path to a file with '.bed' extension";
    public static final String SAM_EXTENSION_EXC =
        "'--sam' parameters must contain a path to a file with '.sam' extension";

    public static final String FASTA_PATH_NOT_EXISTS_EXC = "The '.fasta' file does not exist";
    public static final String BED_PATH_NOT_EXISTS_EXC = "One or more '.bed' files do not exist";
    public static final String SAM_PATH_NOT_EXISTS_EXC = "One or more '.sam' files do not exist";
  }
}
