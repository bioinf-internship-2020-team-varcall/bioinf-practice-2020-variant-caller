package com.epam.bioinf.variantcaller.exceptions.parser.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

public class SamArgsSizeException extends SamParserException {
  public SamArgsSizeException() {
    super(SAM_ARGS_COUNT);
  }

  public SamArgsSizeException(Throwable throwable) {
    super(SAM_ARGS_COUNT, throwable);
  }

  private static final String SAM_ARGS_COUNT =
      "No paths to '.sam' files were presented, must be 1 or more";
}
