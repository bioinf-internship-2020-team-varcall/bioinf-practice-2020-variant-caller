package com.epam.bioinf.variantcaller.exceptions.parser;

import com.epam.bioinf.variantcaller.exceptions.ParserException;

public class OutputDirectoryParserException extends ParserException {
  public OutputDirectoryParserException(String s) {
    super(s);
  }

  public OutputDirectoryParserException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
