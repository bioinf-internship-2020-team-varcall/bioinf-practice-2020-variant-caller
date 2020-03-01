package com.epam.bioinf.variantcaller.cmdline;

import static com.epam.bioinf.variantcaller.cmdline.CommandLineMessages.*;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.io.File.pathSeparatorChar;

public class CommandLineParser {
  private final OptionParser optionParser;
  private final OptionSpec<File> fasta;
  private final OptionSpec<File> bed;
  private final OptionSpec<File> sam;
  private OptionSet options;

  private CommandLineParser() {
    optionParser = new OptionParser() {
      {
        accepts("fasta"); //path to a '.fasta' file
        accepts("bed"); //path to '.bed' files
        accepts("sam"); //path to '.sam' files
      }
    };
    fasta = optionParser.accepts("fasta")
        .withRequiredArg()
        .required()
        .ofType(File.class)
        .withValuesSeparatedBy(pathSeparatorChar);
    bed = optionParser
        .accepts("bed")
        .withRequiredArg()
        .required()
        .ofType(File.class)
        .withValuesSeparatedBy(pathSeparatorChar);
    sam = optionParser
        .accepts("sam")
        .withRequiredArg()
        .required()
        .ofType(File.class)
        .withValuesSeparatedBy(pathSeparatorChar);
  }

  public static CommandLineParser parse(String[] args) throws Exception {
    CommandLineParser parser = new CommandLineParser();
    parser.process(args);
    return parser;
  }

  private void process(String[] args) throws Exception {
    OptionSet tempOptions = optionParser.parse(withRemovedDuplicates(args));
    List<Path> fastaPaths = tempOptions.valuesOf(fasta).stream().map(File::toPath).collect(Collectors.toList());
    List<Path> bedPaths = tempOptions.valuesOf(bed).stream().map(File::toPath).collect(Collectors.toList());
    List<Path> samPaths = tempOptions.valuesOf(sam).stream().map(File::toPath).collect(Collectors.toList());
    String errorMessage = validate(fastaPaths, bedPaths, samPaths);
    if (errorMessage == null) {
      options = tempOptions;
    }
    else {
      throw new Exception(errorMessage);
    }
  }

  public OptionSet getOptions() {
    return options;
  }

  public Path getFastaPath() {
    return options.valuesOf(fasta).get(0).toPath();
  }

  public List<Path> getBedPaths() {
    return options.valuesOf(bed).stream().map(File::toPath).collect(Collectors.toList());
  }

  public List<Path> getSamPaths() {
    return options.valuesOf(sam).stream().map(File::toPath).collect(Collectors.toList());
  }

  private String validate(List<Path> fastaValues, List<Path> bedValues, List<Path> samValues) {
    if (fastaValues.size() != 1) {
      return FASTA_ARGS_COUNT_EXC;
    }
    if (bedValues.size() == 0) return BED_ARGS_COUNT_EXC;
    if (samValues.size() == 0) return SAM_ARGS_COUNT_EXC;

    if (someExtensionIsInvalid(fastaValues, ".fasta")) return FASTA_EXTENSION_EXC;
    if (someExtensionIsInvalid(bedValues, ".bed")) return BED_EXTENSION_EXC;
    if (someExtensionIsInvalid(samValues, ".sam")) return SAM_EXTENSION_EXC;

    if (somePathDoesNotExist(fastaValues)) return FASTA_PATH_NOT_EXISTS_EXC;
    if (somePathDoesNotExist(bedValues)) return BED_PATH_NOT_EXISTS_EXC;
    if (somePathDoesNotExist(samValues)) return SAM_PATH_NOT_EXISTS_EXC;

    return null;
  }

  private boolean somePathDoesNotExist(List<Path> paths) {
    for (Path path : paths) {
      if (!Files.exists(path)) return true;
    }
    return false;
  }

  private boolean someExtensionIsInvalid(List<Path> paths, String extension) {
    for (Path path : paths) {
      if (!path.toString().endsWith(extension)) return true;
    }
    return false;
  }

  private String[] withRemovedDuplicates(String[] args) {
    HashMap<String, Set<String>> hm = new HashMap<>();
    for (int i = 1; i < Arrays.asList(args).size(); i++) {
      if (!args[i].startsWith("--")) {
        if (args[i - 1].startsWith("--") && !hm.containsKey(args[i - 1])) {
          Set<String> elements = new HashSet<>(Arrays.asList(args[i].split("[:;]")));
          hm.put(args[i - 1], elements);
        } else if (hm.containsKey(args[i - 1])) {
          Set<String> elements = hm.get(args[i - 1]);
          elements.addAll(Arrays.asList(args[i].split("[:;]")));
          hm.put(args[i - 1], elements);
        }
      }
    }
    List<String> modifiedArgs = new ArrayList<>();
    hm.keySet().forEach(key -> {
      modifiedArgs.add(key);
      modifiedArgs.add(String.join(":", hm.get(key)));
    });
    return modifiedArgs.toArray(new String[0]);
  }
}
