package com.epam.bioinf.variantcaller.helpers.exceptions.messages;

/**
 * Class holds exception messages
 */
public final class SamHandlerMessages {
  private SamHandlerMessages() {
    // restrict instantiation
  }

  public static final String SAM_FILE_CONTAINS_ONLY_ONE_READ_EXC =
      "Provided SAM file contains only one read";
}
