package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.BED_EXTENSION_EXC;

public class RegionInvalidExtensionException extends RegionParserException {
  public RegionInvalidExtensionException() {
    super(BED_EXTENSION_EXC);
  }

  public RegionInvalidExtensionException(Throwable throwable) {
    super(BED_EXTENSION_EXC, throwable);
  }
}
