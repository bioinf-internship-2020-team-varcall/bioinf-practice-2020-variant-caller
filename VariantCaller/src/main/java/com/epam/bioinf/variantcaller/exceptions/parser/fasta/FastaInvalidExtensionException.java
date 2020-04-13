package com.epam.bioinf.variantcaller.exceptions.parser.fasta;

import com.epam.bioinf.variantcaller.exceptions.parser.FastaParserException;

import static com.epam.bioinf.variantcaller.exceptions.messages.CommandLineParserMessages.FASTA_EXTENSION_EXC;

public class FastaInvalidExtensionException extends FastaParserException {
  public FastaInvalidExtensionException() {
    super(FASTA_EXTENSION_EXC);
  }

  public FastaInvalidExtensionException(Throwable throwable) {
    super(FASTA_EXTENSION_EXC, throwable);
  }
}
