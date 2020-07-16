package com.epam.bioinf.variantcaller.helpers;

public class ProgressBar {

  private static final int BAR_SIZE = 10;
  private final int total;
  private int done;
  private int percentage;
  private int chunks;

  public ProgressBar(int total) {
    this.total = total;
    done = 0;
    percentage = 0;
    chunks = 0;
  }

  public void incrementProgress() {
    done++;
    int newPercentage = (int)((double)done / (double)total * 100);
    if (newPercentage != percentage) {
      percentage = newPercentage;
      if (percentage / BAR_SIZE > chunks) {
        chunks++;
        output();
        if (chunks == BAR_SIZE) {
          System.out.println();
        }
      }
    }
  }

  private void output() {
    char defaultBarChar = '-';
    char doneBarChar = '=';
    String barBuilder = String.valueOf(doneBarChar).repeat(chunks) +
        String.valueOf(defaultBarChar).repeat(BAR_SIZE - chunks);
    System.out.print("Processing reads: " + percentage
        + "% [" + barBuilder + "](" + done + "/" + total + ")\r");
  }
}
