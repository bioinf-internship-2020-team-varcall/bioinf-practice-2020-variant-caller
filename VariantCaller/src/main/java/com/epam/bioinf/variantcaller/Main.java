package com.epam.bioinf.variantcaller;

public class Main {
  public static void main(String[] args) {
    try {
      ParsedData parsedData = ParsedData.createParsedDataFrom(args);
      System.out.println("Job done!");
      System.out.println("Parsed fasta path = " + parsedData.getResultFasta());
      System.out.println("Parsed bed paths = " + parsedData.getResultBed());
      System.out.println("Parsed sam paths = " + parsedData.getResultSam());
    } catch (Exception ex) {
      System.err.println("Job failed with exception " + ex.getLocalizedMessage());
    }
  }
}
