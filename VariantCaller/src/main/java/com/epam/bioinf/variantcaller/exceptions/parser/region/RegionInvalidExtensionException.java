package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

public class RegionInvalidExtensionException extends RegionParserException {
  public RegionInvalidExtensionException() {
    super(BED_EXTENSION);
  }

  public RegionInvalidExtensionException(Throwable throwable) {
    super(BED_EXTENSION, throwable);
  }

  private static final String BED_EXTENSION =
      "'--bed' parameters must contain a path to a file with '.bed' extension";
}
