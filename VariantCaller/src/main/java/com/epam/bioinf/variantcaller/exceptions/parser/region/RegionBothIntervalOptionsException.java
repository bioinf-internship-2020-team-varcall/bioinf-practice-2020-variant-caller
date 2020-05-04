package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

public class RegionBothIntervalOptionsException extends RegionParserException {
  public RegionBothIntervalOptionsException() {
    super(BOTH_INTERVAL_OPTIONS_PROVIDED);
  }

  public RegionBothIntervalOptionsException(Throwable throwable) {
    super(BOTH_INTERVAL_OPTIONS_PROVIDED, throwable);
  }

  private static final String BOTH_INTERVAL_OPTIONS_PROVIDED =
      "Both interval options provided, please specify one";
}
