package com.epam.bioinf.variantcaller.exceptions.parser;

import com.epam.bioinf.variantcaller.exceptions.ParserException;

public class RegionParserException extends ParserException {
  public RegionParserException() {
  }

  public RegionParserException(String s) {
    super(s);
  }

  public RegionParserException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public RegionParserException(Throwable throwable) {
    super(throwable);
  }
}
