package com.epam.bioinf.variantcaller.helpers;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class GradleHelper {
  private GradleHelper() {
    // restrict instantiation
  }

  public static Path getGradleExecutable() {
    String wrapperCallFileName = System.getProperty("os.name").toLowerCase().contains("win") ? "gradlew.bat" : "gradlew";
    return Paths.get(System.getProperty("user.dir"), wrapperCallFileName);
  }
}
