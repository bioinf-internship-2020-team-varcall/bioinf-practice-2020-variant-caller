package com.epam.bioinf.variantcaller.exceptions.parser.region;

import com.epam.bioinf.variantcaller.exceptions.parser.RegionParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.BED_PATH_NOT_EXISTS_EXC;
import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.INVALID_REGION_EXC;

public class RegionPathNotExistsException extends RegionParserException {
  public RegionPathNotExistsException() {
    super(BED_PATH_NOT_EXISTS_EXC);
  }

  public RegionPathNotExistsException(Throwable throwable) {
    super(BED_PATH_NOT_EXISTS_EXC, throwable);
  }
}
