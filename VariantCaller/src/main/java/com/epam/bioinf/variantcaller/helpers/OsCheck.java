package com.epam.bioinf.variantcaller.helpers;

public class OsCheck {
  public enum OS {
    WINDOWS, LINUX, MAC, OTHER
  }

  private static OS os = null;

  public static OS getOS() {
    if (os == null) {
      String operSys = System.getProperty("os.name").toLowerCase();
      if (operSys.contains("mac") || operSys.contains("darwin")) {
        os = OS.MAC;
      } else if (operSys.contains("win")) {
        os = OS.WINDOWS;
      } else if (operSys.contains("nix") || operSys.contains("nux")
          || operSys.contains("aix")) {
        os = OS.LINUX;
      } else {
        os = OS.OTHER;
      }
    }
    return os;
  }

  public static String getGradleExecutable() {
    return OsCheck.getOS() == OS.WINDOWS ? "gradlew.bat" : "gradlew";
  }
}
