package com.epam.bioinf.variantcaller.exceptions;

public class ParserException extends RuntimeException {
  public ParserException() {
  }

  public ParserException(final String s) {
    super(s);
  }

  public ParserException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public ParserException(final Throwable throwable) {
    super(throwable);
  }
}
