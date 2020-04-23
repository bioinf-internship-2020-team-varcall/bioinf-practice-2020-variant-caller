package com.epam.bioinf.variantcaller.exceptions.handlers;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

/**
 * Thrown when something goes wrong while processing FASTA data.
 */
public class FastaHandlerException extends HandlerException {
  public FastaHandlerException(String s) {
    super(s);
  }

  public FastaHandlerException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
