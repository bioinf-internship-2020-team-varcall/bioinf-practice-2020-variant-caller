package com.epam.bioinf.variantcaller.exceptions.handlers.fasta;

import com.epam.bioinf.variantcaller.exceptions.handlers.FastaHandlerException;

import static com.epam.bioinf.variantcaller.exceptions.messages.FastaHandlerMessages.MULTIPLE_SEQUENCES_EXC;

public class FastaMultipleSequencesException extends FastaHandlerException {
  public FastaMultipleSequencesException() {
    super(MULTIPLE_SEQUENCES_EXC);
  }

  public FastaMultipleSequencesException(Throwable throwable) {
    super(MULTIPLE_SEQUENCES_EXC, throwable);
  }
}
