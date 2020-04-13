package com.epam.bioinf.variantcaller.exceptions.parser.fasta;

import com.epam.bioinf.variantcaller.exceptions.parser.FastaParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.FASTA_ARGS_COUNT_EXC;

public class FastaArgsSizeException extends FastaParserException {
  public FastaArgsSizeException() {
    super(FASTA_ARGS_COUNT_EXC);
  }

  public FastaArgsSizeException(Throwable throwable) {
    super(FASTA_ARGS_COUNT_EXC, throwable);
  }
}
