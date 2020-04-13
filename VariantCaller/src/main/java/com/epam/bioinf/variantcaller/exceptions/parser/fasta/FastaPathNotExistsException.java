package com.epam.bioinf.variantcaller.exceptions.parser.fasta;

import com.epam.bioinf.variantcaller.exceptions.parser.FastaParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.FASTA_PATH_NOT_EXISTS_EXC;

public class FastaPathNotExistsException extends FastaParserException {
  public FastaPathNotExistsException() {
    super(FASTA_PATH_NOT_EXISTS_EXC);
  }

  public FastaPathNotExistsException(Throwable throwable) {
    super(FASTA_PATH_NOT_EXISTS_EXC, throwable);
  }
}
