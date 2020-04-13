package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.BOTH_INTERVAL_OPTIONS_PROVIDED_EXC;

public class RegionBothIntervalOptionsException extends RegionParserException {
  public RegionBothIntervalOptionsException() {
    super(BOTH_INTERVAL_OPTIONS_PROVIDED_EXC);
  }

  public RegionBothIntervalOptionsException(Throwable throwable) {
    super(BOTH_INTERVAL_OPTIONS_PROVIDED_EXC, throwable);
  }
}
