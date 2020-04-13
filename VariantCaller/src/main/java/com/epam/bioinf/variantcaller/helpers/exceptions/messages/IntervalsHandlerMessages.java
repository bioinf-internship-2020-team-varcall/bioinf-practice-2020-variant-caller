package com.epam.bioinf.variantcaller.helpers.exceptions.messages;

/**
 * Class holds exception messages for IntervalsHandler
 */
public class IntervalsHandlerMessages {
  private IntervalsHandlerMessages() {
    // restrict instantiation
  }

  public static final String INVALID_REGION_EXC =
      "Could not parse interval points, please verify they are valid";
  public static final String INTERVAL_START_EXC =
      "Invalid interval start parsed from file";
  public static final String INTERVAL_END_EXC =
      "Invalid interval end parsed from file";
  public static final String ERROR_READING_EXC =
      "Problem reading intervals, file is malformed, please verify your .bed file";
}
