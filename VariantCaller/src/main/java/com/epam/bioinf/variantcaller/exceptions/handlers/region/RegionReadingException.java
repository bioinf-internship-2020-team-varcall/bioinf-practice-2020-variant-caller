package com.epam.bioinf.variantcaller.exceptions.handlers.region;

import com.epam.bioinf.variantcaller.exceptions.handlers.RegionHandlerException;

public class RegionReadingException extends RegionHandlerException {
  public RegionReadingException() {
    super(ERROR_READING);
  }

  public RegionReadingException(Throwable throwable) {
    super(ERROR_READING, throwable);
  }

  private static final String ERROR_READING =
      "Problem reading intervals, file is malformed, please verify your .bed file";
}
