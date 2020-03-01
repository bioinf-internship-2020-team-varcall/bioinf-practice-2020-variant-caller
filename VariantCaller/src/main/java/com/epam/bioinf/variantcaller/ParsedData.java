package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;

import java.nio.file.Path;
import java.util.List;

public class ParsedData {
  private final Path fastaPath;
  private final List<Path> bedPaths;
  private final List<Path> samPaths;

  private ParsedData(CommandLineParser parser) {
    fastaPath = parser.getFastaPath();
    bedPaths = parser.getBedPaths();
    samPaths = parser.getSamPaths();
  }

  public static ParsedData createParsedDataFrom(String[] args) throws Exception {
    return new ParsedData(CommandLineParser.parse(args));
  }

  public Path getResultFasta() {
    return fastaPath;
  }

  public List<Path> getResultBed() {
    return bedPaths;
  }

  public List<Path> getResultSam() {
    return samPaths;
  }
}
