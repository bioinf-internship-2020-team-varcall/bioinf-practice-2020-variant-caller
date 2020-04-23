package com.epam.bioinf.variantcaller.exceptions.parser;

import com.epam.bioinf.variantcaller.exceptions.ParserException;


/**
 * Thrown when something goes wrong while parsing SAM data.
 */
public class SamParserException extends ParserException {
  public SamParserException(String s) {
    super(s);
  }

  public SamParserException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
