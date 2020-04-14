package com.epam.bioinf.variantcaller.exceptions.handlers;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

public class FastaHandlerException extends HandlerException {
  public FastaHandlerException(String s) {
    super(s);
  }

  public FastaHandlerException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
