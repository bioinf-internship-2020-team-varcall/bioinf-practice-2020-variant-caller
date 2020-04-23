package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

public class RegionPathNotExistsException extends RegionParserException {
  public RegionPathNotExistsException() {
    super(BED_PATH_NOT_EXISTS);
  }

  public RegionPathNotExistsException(Throwable throwable) {
    super(BED_PATH_NOT_EXISTS, throwable);
  }

  private static final String BED_PATH_NOT_EXISTS = "One or more '.bed' files do not exist";
}
