package com.epam.bioinf.variantcaller.exceptions;

/**
 * Thrown when something goes wrong while parsing data.
 */
public class ParserException extends RuntimeException {
  public ParserException(final String s) {
    super(s);
  }

  public ParserException(final String s, final Throwable throwable) {
    super(s, throwable);
  }
}
