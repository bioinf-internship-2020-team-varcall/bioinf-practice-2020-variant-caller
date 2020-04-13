package com.epam.bioinf.variantcaller.exceptions.parser.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.SAM_EXTENSION_EXC;

public class SamInvalidExtensionException extends SamParserException {
  public SamInvalidExtensionException() {
    super(SAM_EXTENSION_EXC);
  }

  public SamInvalidExtensionException(Throwable throwable) {
    super(SAM_EXTENSION_EXC, throwable);
  }
}
