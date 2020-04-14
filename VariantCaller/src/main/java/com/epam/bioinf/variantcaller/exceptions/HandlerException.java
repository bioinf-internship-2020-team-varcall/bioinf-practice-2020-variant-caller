package com.epam.bioinf.variantcaller.exceptions;

/**
 * Thrown when something goes wrong while processing data.
 */
public class HandlerException extends RuntimeException {
  public HandlerException(final String s) {
    super(s);
  }

  public HandlerException(final String s, final Throwable throwable) {
    super(s, throwable);
  }
}
