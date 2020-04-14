package com.epam.bioinf.variantcaller.exceptions.handlers;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

/**
 * Thrown when something goes wrong while processing SAM data.
 *
 * This class is not in use now but will be in later versions.
 */
public class SamHandlerException extends HandlerException {
  public SamHandlerException(String s) {
    super(s);
  }

  public SamHandlerException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
