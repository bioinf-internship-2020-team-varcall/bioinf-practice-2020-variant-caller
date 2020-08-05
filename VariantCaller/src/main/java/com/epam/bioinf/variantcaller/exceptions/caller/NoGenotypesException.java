package com.epam.bioinf.variantcaller.exceptions.caller;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

/**
 * Thrown when something goes wrong while processing region data.
 */
public class NoGenotypesException extends HandlerException {
  public NoGenotypesException(String s) {
    super(s);
  }

  public NoGenotypesException(String s, Throwable throwable) {
    super(s, throwable);
  }
}

