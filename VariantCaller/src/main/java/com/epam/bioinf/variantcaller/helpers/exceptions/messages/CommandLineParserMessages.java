package com.epam.bioinf.variantcaller.helpers.exceptions.messages;

/**
 * Class holds exception messages for CommandLineParser
 */
public final class CommandLineParserMessages {
  private CommandLineParserMessages() {
    // restrict instantiation
  }

  public static final String FASTA_ARGS_COUNT_EXC =
      "Multiple or no paths to '.fasta' files were presented, must be 1";
  public static final String BED_ARGS_COUNT_EXC = "" +
      "No paths to '.bed' files were presented, must be 1 or more";
  public static final String SAM_ARGS_COUNT_EXC =
      "No paths to '.sam' files were presented, must be 1 or more";

  public static final String BOTH_INTERVAL_OPTIONS_PROVIDED_EXC =
      "Both interval options provided, please specify one";
  public static final String INVALID_REGION_EXC =
      "Region specified incorrectly";

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
