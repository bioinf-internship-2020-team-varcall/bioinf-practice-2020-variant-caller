package com.epam.bioinf.variantcaller.exceptions.parser.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

public class SamPathNotExistsException extends SamParserException {
  public SamPathNotExistsException() {
    super(SAM_PATH_NOT_EXISTS);
  }

  public SamPathNotExistsException(Throwable throwable) {
    super(SAM_PATH_NOT_EXISTS, throwable);
  }

  private static final String SAM_PATH_NOT_EXISTS = "One or more '.sam' files do not exist";
}
