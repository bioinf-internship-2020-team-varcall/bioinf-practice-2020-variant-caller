package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

import static com.epam.bioinf.variantcaller.exceptions.messages.IntervalsHandlerMessages.INTERVAL_START_EXC;

public class RegionIllegalStartException extends RegionHandlerException {
  public RegionIllegalStartException() {
    super(INTERVAL_START_EXC);
  }

  public RegionIllegalStartException(Throwable throwable) {
    super(INTERVAL_START_EXC, throwable);
  }
}
