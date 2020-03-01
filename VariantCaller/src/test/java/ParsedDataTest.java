import com.epam.bioinf.variantcaller.ParsedData;
import joptsimple.OptionException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;

import static org.junit.rules.ExpectedException.none;

public class ParsedDataTest {
  private final String currentPathString = Paths.get("src/test/resources").toAbsolutePath().toString();

  @Rule
  public final ExpectedException thrown = none();

  @Test
  public void parsedDataMustFailWithInvalidParameters() {
    String[] invalidTestArgs = {
        "-p", currentPathString + "/test1.fasta",
        "-ap", currentPathString + "/test1.bed",
        "-hp", currentPathString + "/test1.sam"
    };
    try {
      ParsedData.createParsedDataFrom(invalidTestArgs);
    } catch (Exception ex) {
      thrown.expect(OptionException.class);
    }
  }
}
