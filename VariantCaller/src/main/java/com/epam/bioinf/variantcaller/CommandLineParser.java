package com.epam.bioinf.variantcaller;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.List;

//This is temporary example command line parser, which will be changed in the future versions
public class CommandLineParser {
  private OptionParser parser;
  private OptionSpec<String> a;
  private OptionSet options;

  public CommandLineParser(String[] args) throws Exception {
    parser = new OptionParser();
    parser.accepts("a");
    a = parser.accepts("a").withRequiredArg();
    options = parser.parse(args);
  }

  public OptionSet getOptions() {
    return options;
  }

  public List<String> getA() {
    return options.valuesOf(a);
  }
}
