package com.epam.bioinf.variantcaller.helpers.exceptions.messages;

public final class SamHandlerMessages {
  private SamHandlerMessages() {
    // restrict instantiation
  }

  public static final String SAM_FILES_PATHS_LIST_EMPTY =
      "Provided SAM files list is empty";
  public static final String SAM_FILE_CONTAINS_ONLY_ONE_READ =
      "Provided SAM file contains only one read";
}
