package com.epam.bioinf.variantcaller.exceptions.parser;

import com.epam.bioinf.variantcaller.exceptions.ParserException;

/**
 * Thrown when something goes wrong while parsing region data.
 */
public class RegionParserException extends ParserException {
  public RegionParserException(String s) {
    super(s);
  }

  public RegionParserException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
