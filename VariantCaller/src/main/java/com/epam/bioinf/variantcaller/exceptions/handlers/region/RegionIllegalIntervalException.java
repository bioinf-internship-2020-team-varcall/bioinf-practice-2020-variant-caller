package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

import static com.epam.bioinf.variantcaller.exceptions.messages.IntervalsHandlerMessages.INVALID_REGION_EXC;

public class RegionIllegalIntervalException extends RegionHandlerException {
  public RegionIllegalIntervalException() {
    super(INVALID_REGION_EXC);
  }

  public RegionIllegalIntervalException(Throwable throwable) {
    super(INVALID_REGION_EXC, throwable);
  }
}
