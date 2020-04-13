package com.epam.bioinf.variantcaller.exceptions.parser;

import com.epam.bioinf.variantcaller.exceptions.ParserException;

public class FastaParserException extends ParserException {
  public FastaParserException() {
  }

  public FastaParserException(String s) {
    super(s);
  }

  public FastaParserException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public FastaParserException(Throwable throwable) {
    super(throwable);
  }
}
