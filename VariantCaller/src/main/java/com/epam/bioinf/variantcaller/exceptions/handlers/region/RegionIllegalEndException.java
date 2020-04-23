package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

public class RegionIllegalEndException extends RegionHandlerException {
  public RegionIllegalEndException() {
    super(INTERVAL_END);
  }

  public RegionIllegalEndException(Throwable throwable) {
    super(INTERVAL_END, throwable);
  }

  private static final String INTERVAL_END = "Invalid interval end parsed from file";
}
