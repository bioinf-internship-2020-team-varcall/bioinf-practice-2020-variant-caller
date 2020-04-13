package com.epam.bioinf.variantcaller.exceptions.handlers;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

public class SamHandlerException extends HandlerException {
  public SamHandlerException() {
  }

  public SamHandlerException(String s) {
    super(s);
  }

  public SamHandlerException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public SamHandlerException(Throwable throwable) {
    super(throwable);
  }
}
