package com.epam.bioinf.variantcaller.helpers.exceptions.messages;

public final class SamHandlerMessages {
  private SamHandlerMessages() {
    // restrict instantiation
  }

  public static final String SAM_FILES_PATHS_LIST_EMPTY_EXC =
      "Provided SAM files list is empty";
  public static final String SAM_FILE_CONTAINS_ONLY_ONE_READ_EXC =
      "Provided SAM file contains only one read";
  public static final String SAM_INVALID_EXTENSION_EXC =
      "Argument must contain a path to a file with '.sam' extension";
  public static final String SAM_PATH_NOT_EXISTS_EXC =
      "One or more '.sam' files do not exist";
}
