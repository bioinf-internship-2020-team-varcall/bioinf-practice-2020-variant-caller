package com.epam.bioinf.variantcaller.exceptions.parser.fasta;

import com.epam.bioinf.variantcaller.exceptions.parser.FastaParserException;

public class FastaArgsSizeException extends FastaParserException {
  public FastaArgsSizeException() {
    super(FASTA_ARGS_COUNT);
  }

  public FastaArgsSizeException(Throwable throwable) {
    super(FASTA_ARGS_COUNT, throwable);
  }

  private static final String FASTA_ARGS_COUNT =
      "Multiple or no paths to '.fasta' files were presented, must be 1";
}
