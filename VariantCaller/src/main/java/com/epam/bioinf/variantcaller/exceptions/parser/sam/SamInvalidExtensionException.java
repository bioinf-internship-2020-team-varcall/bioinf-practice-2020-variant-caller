package com.epam.bioinf.variantcaller.exceptions.parser.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

public class SamInvalidExtensionException extends SamParserException {
  public SamInvalidExtensionException() {
    super(SAM_EXTENSION);
  }

  public SamInvalidExtensionException(Throwable throwable) {
    super(SAM_EXTENSION, throwable);
  }

  private static final String SAM_EXTENSION =
      "'--sam' parameters must contain a path to a file with '.sam' extension";
}
