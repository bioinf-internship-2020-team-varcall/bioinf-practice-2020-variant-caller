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
  public void intervalsHandlerMustReturnCorrectIntervalWithCmdLineInput() {
    String expected = "chr1";
    IntervalsHandler testHandler = new IntervalsHandler("chr1 12 123");
    assertEquals(expected, testHandler.getIntervals().get(0).getContig());
  }

  @Test
  public void intervalsHandlerMustReturnCorrectIntervalWithSingleFile() {
    List<String> expectedList = Arrays.asList("chr1", "chr1", "chr1", "chr1", "chr1", "chr2", "chr2");
    List<Path> testPaths = Collections.singletonList(Paths.get(testFilePath("test1.bed")));
    IntervalsHandler testHandler = new IntervalsHandler(testPaths);
    assertArrayEquals(expectedList.toArray(), testHandler.getIntervals().stream().map(BEDFeature::getContig).toArray());
  }

}
