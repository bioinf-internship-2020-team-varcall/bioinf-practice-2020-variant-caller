package com.epam.bioinf.variantcaller.exceptions.handlers;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

public class RegionHandlerException extends HandlerException {
  public RegionHandlerException() {
  }

  public RegionHandlerException(String s) {
    super(s);
  }

  public RegionHandlerException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public RegionHandlerException(Throwable throwable) {
    super(throwable);
  }
}
