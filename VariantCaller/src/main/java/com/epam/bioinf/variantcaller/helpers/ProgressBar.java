package com.epam.bioinf.variantcaller.helpers;

public class ProgressBar {

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

  public void process() {
    done++;
    int newPercentage = (int)((double)done / (double)total * 100);
    if (newPercentage != percentage) {
      percentage = newPercentage;
      if (percentage / 10 > chunks) {
        chunks++;
        output();
        if (chunks == 10) {
          System.out.println();
        }
      }
    }
  }

  private void output() {
    int barSize = 10;
    char defaultBarChar = '-';
    char doneBarChar = '=';
    String barBuilder = String.valueOf(doneBarChar).repeat(chunks) +
        String.valueOf(defaultBarChar).repeat(barSize - chunks);
    System.out.print("Processing reads: " + percentage + "% [" + barBuilder + "](" + done + "/" + total +")\r");
  }
}
