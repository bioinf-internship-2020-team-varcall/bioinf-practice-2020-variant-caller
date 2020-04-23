package com.epam.bioinf.variantcaller.exceptions.parser.fasta;

import com.epam.bioinf.variantcaller.exceptions.parser.FastaParserException;

public class FastaPathNotExistsException extends FastaParserException {
  public FastaPathNotExistsException() {
    super(FASTA_PATH_NOT_EXISTS);
  }

  public FastaPathNotExistsException(Throwable throwable) {
    super(FASTA_PATH_NOT_EXISTS, throwable);
  }

  private static final String FASTA_PATH_NOT_EXISTS = "The '.fasta' file does not exist";
}
