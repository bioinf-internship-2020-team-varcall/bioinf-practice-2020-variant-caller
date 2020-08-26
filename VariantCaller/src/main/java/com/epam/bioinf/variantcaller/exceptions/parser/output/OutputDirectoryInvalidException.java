package com.epam.bioinf.variantcaller.exceptions.parser.output;

import com.epam.bioinf.variantcaller.exceptions.parser.OutputDirectoryParserException;

public class OutputDirectoryInvalidException extends OutputDirectoryParserException {
  public OutputDirectoryInvalidException() {
    super(PROVIDED_PATH_IS_INCORRECT);
  }

  public OutputDirectoryInvalidException(Throwable throwable) {
    super(PROVIDED_PATH_IS_INCORRECT, throwable);
  }

  private static final String PROVIDED_PATH_IS_INCORRECT =
      "Provided path is incorrect or is not a directory";
}
