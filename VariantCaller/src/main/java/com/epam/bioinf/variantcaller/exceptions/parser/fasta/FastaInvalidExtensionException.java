package com.epam.bioinf.variantcaller.exceptions.parser.fasta;

import com.epam.bioinf.variantcaller.exceptions.parser.FastaParserException;

public class FastaInvalidExtensionException extends FastaParserException {
  public FastaInvalidExtensionException() {
    super(FASTA_EXTENSION);
  }

  public FastaInvalidExtensionException(Throwable throwable) {
    super(FASTA_EXTENSION, throwable);
  }

  private static final String FASTA_EXTENSION =
      "'--fasta' parameters must contain a path to a file with '.fasta' extension";
}
