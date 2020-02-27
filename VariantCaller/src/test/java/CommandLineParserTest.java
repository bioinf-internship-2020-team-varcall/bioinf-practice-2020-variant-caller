import com.epam.bioinf.variantcaller.CommandLineParser;
import joptsimple.OptionSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineParserTest {

  //This is temporary example test, which will be replaced in the future versions
  @Test
  public void optionParserShouldAcceptParameters() {
    String[] testArgs = {"-a", "one"};
    try {
      CommandLineParser testParser = new CommandLineParser(testArgs);
      OptionSet parserOptions = testParser.getOptions();
      assertTrue(parserOptions.has("a"), "Parser must accept -a parameter");
      assertEquals("one", testParser.getA().get(0), "-a must be 'one'");
    } catch (Exception e) {
      fail();
    }
  }
}
