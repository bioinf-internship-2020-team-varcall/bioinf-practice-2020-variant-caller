package com.epam.bioinf.variantcaller.helpers;

import java.io.PrintStream;

public class ProgressBar {

  private static final String DEFAULT_BAR_CHAR = "-";
  private static final String DONE_BAR_CHAR = "=";
  private static final int BAR_SIZE = 10;
  private final int total;
  private int done;
  private int percentage;
  private int chunks;
  private final PrintStream outputStream;

  public ProgressBar(int total, PrintStream outputStream) {
    this.total = total;
    this.outputStream = outputStream;
    done = 0;
    percentage = 0;
    chunks = 0;
  }

  public void incrementProgress() {
    done++;
    if (done > total) {
      throw new IllegalStateException(
          "ProgressBar can not be incremented more than set total value!");
    }
    int newPercentage = (int) ((double) done / (double) total * 100);
    if (newPercentage != percentage) {
      percentage = newPercentage;
      if (percentage / BAR_SIZE > chunks) {
        chunks++;
        output();
        if (chunks == BAR_SIZE) {
          outputStream.println();
        }
      }
    }
  }

  private void output() {
    String barBuilder = DONE_BAR_CHAR.repeat(chunks) +
        DEFAULT_BAR_CHAR.repeat(BAR_SIZE - chunks);
    outputStream.print(
        String.format(
            "Processing reads: %d%% [%s](%d/%d)\r",
            percentage,
            barBuilder,
            done,
            total
        )
    );
  }
}
