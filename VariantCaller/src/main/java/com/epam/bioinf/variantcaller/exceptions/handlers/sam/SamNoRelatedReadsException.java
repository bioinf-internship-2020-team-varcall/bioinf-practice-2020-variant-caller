package com.epam.bioinf.variantcaller.exceptions.handlers.sam;

import com.epam.bioinf.variantcaller.exceptions.parser.SamParserException;

public class SamNoRelatedReadsException extends SamParserException {
  public SamNoRelatedReadsException() {
    super(SAM_NO_RELATED_READS);
  }

  public SamNoRelatedReadsException(Throwable throwable) {
    super(SAM_NO_RELATED_READS, throwable);
  }

  private static final String SAM_NO_RELATED_READS =
      "None of the reads are related to the provided intervals";
}
