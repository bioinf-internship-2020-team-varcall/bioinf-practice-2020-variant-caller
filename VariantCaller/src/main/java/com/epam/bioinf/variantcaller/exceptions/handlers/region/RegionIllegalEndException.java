package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

import static com.epam.bioinf.variantcaller.exceptions.messages.IntervalsHandlerMessages.INTERVAL_END_EXC;

public class RegionIllegalEndException extends RegionHandlerException {
  public RegionIllegalEndException() {
    super(INTERVAL_END_EXC);
  }

  public RegionIllegalEndException(Throwable throwable) {
    super(INTERVAL_END_EXC, throwable);
  }
}
