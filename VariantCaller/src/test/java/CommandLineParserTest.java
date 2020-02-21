import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.jupiter.api.Test;

public class CommandLineParserTest {

    @Test
    public void optionParserShouldAcceptParameters() {
        String[] testArgs = {"-a", "one", "-b", "two", "-c", "three"};
        CommandLineParser testParser = new CommandLineParser(testArgs);

        OptionSet parserOptions = testParser.getOptions();
        assertTrue(parserOptions.has("a"), "Parser must accept -a parameter");
        assertTrue(parserOptions.has("b"), "Parser must accept -b parameter");
        assertTrue(parserOptions.has("c"), "Parser must accept -c parameter");

        assertEquals("one", testParser.getA().get(0), "-a must be 'one'");
        assertEquals("two", testParser.getB().get(0), "-b must be 'two'");
        assertEquals("three", testParser.getC().get(0), "-c must be 'three'");
    }
}