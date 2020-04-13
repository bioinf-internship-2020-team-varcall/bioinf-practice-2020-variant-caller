package com.epam.bioinf.variantcaller.exceptions.messages;

/**
 * Class holds exception messages for FastaHandler
 */
public final class FastaHandlerMessages {
  private FastaHandlerMessages() {
    // restrict instantiation
  }

  public static final String MULTIPLE_SEQUENCES_EXC =
      "Multiple fasta sequences were provided, fasta file must contain only one sequence";
}
