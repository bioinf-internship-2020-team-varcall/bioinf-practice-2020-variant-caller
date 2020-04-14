package com.epam.bioinf.variantcaller.exceptions.handlers;

import com.epam.bioinf.variantcaller.exceptions.HandlerException;

/**
 * Thrown when something goes wrong while processing region data.
 */
public class RegionHandlerException extends HandlerException {
  public RegionHandlerException(String s) {
    super(s);
  }

  public RegionHandlerException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
