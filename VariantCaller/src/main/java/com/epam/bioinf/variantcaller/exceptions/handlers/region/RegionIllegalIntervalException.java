package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

public class RegionIllegalIntervalException extends RegionHandlerException {
  public RegionIllegalIntervalException() {
    super(INVALID_REGION);
  }

  public RegionIllegalIntervalException(Throwable throwable) {
    super(INVALID_REGION, throwable);
  }

  private static final String INVALID_REGION =
      "Could not parse interval points, please verify they are valid";
}
