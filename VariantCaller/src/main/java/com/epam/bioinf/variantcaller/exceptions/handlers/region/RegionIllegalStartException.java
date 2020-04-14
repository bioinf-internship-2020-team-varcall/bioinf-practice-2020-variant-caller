package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

public class RegionIllegalStartException extends RegionHandlerException {
  public RegionIllegalStartException() {
    super(INTERVAL_START);
  }

  public RegionIllegalStartException(Throwable throwable) {
    super(INTERVAL_START, throwable);
  }

  private static final String INTERVAL_START = "Invalid interval start parsed from file";
}
