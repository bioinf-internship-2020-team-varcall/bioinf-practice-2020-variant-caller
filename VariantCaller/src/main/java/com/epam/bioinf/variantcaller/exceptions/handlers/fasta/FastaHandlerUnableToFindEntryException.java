package com.epam.bioinf.variantcaller.exceptions.handlers.fasta;

import com.epam.bioinf.variantcaller.exceptions.handlers.FastaHandlerException;

public class FastaHandlerUnableToFindEntryException extends FastaHandlerException {
  public FastaHandlerUnableToFindEntryException(String message) {
    super(message);
  }

  public FastaHandlerUnableToFindEntryException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
