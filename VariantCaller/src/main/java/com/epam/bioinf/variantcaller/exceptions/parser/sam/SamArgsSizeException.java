package com.epam.bioinf.variantcaller.exceptions.parser.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.SAM_ARGS_COUNT_EXC;

public class SamArgsSizeException extends SamParserException {
  public SamArgsSizeException() {
    super(SAM_ARGS_COUNT_EXC);
  }

  public SamArgsSizeException(Throwable throwable) {
    super(SAM_ARGS_COUNT_EXC, throwable);
  }
}
