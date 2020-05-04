package com.epam.bioinf.variantcaller.exceptions.parser;

import com.epam.bioinf.variantcaller.exceptions.ParserException;

/**
 * Thrown when something goes wrong while parsing FASTA data.
 */
public class FastaParserException extends ParserException {
  public FastaParserException(String s) {
    super(s);
  }

  public FastaParserException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
