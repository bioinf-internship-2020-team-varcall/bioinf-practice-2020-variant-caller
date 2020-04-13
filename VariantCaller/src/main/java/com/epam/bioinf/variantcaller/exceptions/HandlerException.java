package com.epam.bioinf.variantcaller.exceptions;

public class HandlerException extends RuntimeException {
  public HandlerException() {
  }

  public HandlerException(final String s) {
    super(s);
  }

  public HandlerException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public HandlerException(final Throwable throwable) {
    super(throwable);
  }
}
