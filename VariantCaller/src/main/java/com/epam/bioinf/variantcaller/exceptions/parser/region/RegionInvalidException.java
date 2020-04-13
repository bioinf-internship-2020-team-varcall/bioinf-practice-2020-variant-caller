package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.INVALID_REGION_EXC;

public class RegionInvalidException extends RegionParserException {
  public RegionInvalidException() {
    super(INVALID_REGION_EXC);
  }

  public RegionInvalidException(Throwable throwable) {
    super(INVALID_REGION_EXC, throwable);
  }
}
