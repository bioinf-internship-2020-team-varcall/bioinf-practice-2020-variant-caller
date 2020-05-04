package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

public class RegionInvalidException extends RegionParserException {
  public RegionInvalidException() {
    super(INVALID_REGION);
  }

  public RegionInvalidException(Throwable throwable) {
    super(INVALID_REGION, throwable);
  }

  private static final String INVALID_REGION =
      "Region specified incorrectly";

}
