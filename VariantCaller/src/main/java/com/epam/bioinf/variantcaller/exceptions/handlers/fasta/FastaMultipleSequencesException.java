package com.epam.bioinf.variantcaller.exceptions.handlers.fasta;

import com.epam.bioinf.variantcaller.exceptions.handlers.FastaHandlerException;

public class FastaMultipleSequencesException extends FastaHandlerException {
  public FastaMultipleSequencesException() {
    super(MULTIPLE_SEQUENCES);
  }

  private static final String MULTIPLE_SEQUENCES =
      "Multiple fasta sequences were provided, fasta file must contain only one sequence";
}
