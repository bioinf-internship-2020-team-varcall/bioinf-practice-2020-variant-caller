package com.epam.bioinf.variantcaller.helpers;

public final class GradleHelper {
  private GradleHelper() {
    // restrict instantiation
  }

  public static String getGradleExecutable() {
    return System.getProperty("os.name").toLowerCase().contains("win") ?
        "gradlew.bat" : "gradlew";
  }
}
