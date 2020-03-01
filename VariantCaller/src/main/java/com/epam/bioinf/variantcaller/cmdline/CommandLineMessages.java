package com.epam.bioinf.variantcaller.cmdline;

public class CommandLineMessages {
    private CommandLineMessages() {
      // restrict instantiation
    }

  public final static String FASTA_ARGS_COUNT_EXC = "Multiple or no paths to '.fasta' files were presented, must be 1";
  public final static String BED_ARGS_COUNT_EXC = "No paths to '.bed' files were presented, must be 1 or more";
  public final static String SAM_ARGS_COUNT_EXC = "No paths to '.sam' files were presented, must be 1 or more";

  public final static String FASTA_EXTENSION_EXC = "'--fasta' parameters must contain a path to a file with '.fasta' extension";
  public final static String BED_EXTENSION_EXC = "'--bed' parameters must contain a path to a file with '.bed' extension";
  public final static String SAM_EXTENSION_EXC = "'--sam' parameters must contain a path to a file with '.sam' extension";

  public final static String FASTA_PATH_NOT_EXISTS_EXC = "The '.fasta' file does not exist";
  public final static String BED_PATH_NOT_EXISTS_EXC = "One or more '.bed' files do not exist";
  public final static String SAM_PATH_NOT_EXISTS_EXC = "One or more '.sam' files do not exist";
}
