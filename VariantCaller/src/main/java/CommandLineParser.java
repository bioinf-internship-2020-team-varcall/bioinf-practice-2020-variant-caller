import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.List;

public class CommandLineParser {
    private OptionParser parser;
    private OptionSpec<String> a;
    private OptionSpec<String> b;
    private OptionSpec<String> c;
    private OptionSet options;

    public CommandLineParser(String[] args) {
        try {
            parser = new OptionParser();
            parser.accepts("a");
            parser.accepts("b");
            parser.accepts("c");
            a = parser.accepts("a").withRequiredArg();
            b = parser.accepts("b").withRequiredArg();
            c = parser.accepts("c").withRequiredArg();
            options = parser.parse(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OptionSet getOptions() {
        return options;
    }

    public List<String> getA() {
        return options.valuesOf(a);
    }

    public List<String> getB() {
        return options.valuesOf(b);
    }

    public List<String> getC() {
        return options.valuesOf(c);
    }
}
