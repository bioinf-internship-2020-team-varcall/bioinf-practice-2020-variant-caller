import com.epam.bioinf.variantcaller.handlers.IntervalsHandler;

import htsjdk.tribble.bed.BEDFeature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.epam.bioinf.variantcaller.helpers.TestHelper.testFilePath;
import static org.junit.jupiter.api.Assertions.*;

public class IntervalsHandlerTest {

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalsNumberWithCmdLineInput() {
    final long expectedIntervalsNumber = 1;
    IntervalsHandler testHandler = new IntervalsHandler("chr1 12 123");
    assertEquals(expectedIntervalsNumber, testHandler.getIntervals().stream().count());
  }

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalsNumberWithSingleFile() {
    final long expectedIntervalsNumber = 7;
    List<Path> testPaths = Collections.singletonList(Paths.get(testFilePath("test1.bed")));
    IntervalsHandler testHandler = new IntervalsHandler(testPaths);
    assertEquals(expectedIntervalsNumber, testHandler.getIntervals().stream().count());
  }

  @Test void intervalsHandlerMustReturnCorrectIntervalsNumberWithMultipleFiles() {
    final long expectedIntervalsNumber = 16;
    List<Path> testPaths = Arrays.asList(Paths.get(testFilePath("test1.bed")),
        Paths.get(testFilePath("test2.bed")));
    IntervalsHandler testHandler = new IntervalsHandler(testPaths);
    assertEquals(expectedIntervalsNumber, testHandler.getIntervals().stream().count());
  }

}
