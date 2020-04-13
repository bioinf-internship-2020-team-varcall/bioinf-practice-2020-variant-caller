package com.epam.bioinf.variantcaller.exceptions.parser.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.SAM_PATH_NOT_EXISTS_EXC;

public class SamPathNotExistsException extends SamParserException {
  public SamPathNotExistsException() {
    super(SAM_PATH_NOT_EXISTS_EXC);
  }

  public SamPathNotExistsException(Throwable throwable) {
    super(SAM_PATH_NOT_EXISTS_EXC, throwable);
  }
}
