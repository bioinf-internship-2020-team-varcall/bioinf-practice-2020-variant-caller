package com.epam.bioinf.variantcaller;

import com.epam.bioinf.variantcaller.cmdline.CommandLineParser;
import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.handlers.SamHandler;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

import java.nio.file.Path;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    ParsedArguments parsedArguments = CommandLineParser.parse(args);

    SamHandler handler = new SamHandler(parsedArguments.getSamPaths());
    Map<Path, Long> map =  handler.computeReadsByPaths();
    map.forEach((path, reads) -> System.out.println(path + " : " + reads));

    System.out.println("Parsed fasta path = " + parsedArguments.getFastaPath());
    System.out.println("Parsed bed paths = " + parsedArguments.getBedPaths());
    System.out.println("Parsed sam paths = " + parsedArguments.getSamPaths());
  }
}
