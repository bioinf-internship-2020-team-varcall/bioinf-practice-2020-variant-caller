package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

import static com.epam.bioinf.variantcaller.exceptions.messages.FastaHandlerMessages.MULTIPLE_SEQUENCES_EXC;
import static com.epam.bioinf.variantcaller.exceptions.messages.IntervalsHandlerMessages.ERROR_READING_EXC;

public class RegionReadingException extends RegionHandlerException {
  public RegionReadingException() {
    super(ERROR_READING_EXC);
  }

  public RegionReadingException(Throwable throwable) {
    super(MULTIPLE_SEQUENCES_EXC, throwable);
  }
}
