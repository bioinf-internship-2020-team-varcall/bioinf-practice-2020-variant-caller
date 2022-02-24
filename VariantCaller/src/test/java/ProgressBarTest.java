import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProgressBarTest {

  @ParameterizedTest
  @MethodSource("provideArgumentsForMustCorrectlyIncrementProgress")
  public void progressBarMustCorrectlyIncrementProgress(
      int total, int checkValue, String expectedOutput) {
    final ByteArrayOutputStream progressBarOutputStream = new ByteArrayOutputStream();
    ProgressBar progressBar = new ProgressBar(total, new PrintStream(progressBarOutputStream,
        true, Charset.defaultCharset()));
    for (int i = 0; i < checkValue; i++) {
      progressBar.incrementProgress();
    }
    assertEquals(expectedOutput, progressBarOutputStream.toString(Charset.defaultCharset()));
  }

  @Test
  public void progressBarMustFailIfTotalValueOverflown() {
    ProgressBar progressBar = new ProgressBar(0, new PrintStream(OutputStream.nullOutputStream(),
        true, Charset.defaultCharset()));
    assertThrows(IllegalStateException.class, progressBar::incrementProgress);
  }

  private static Stream<Arguments> provideArgumentsForMustCorrectlyIncrementProgress() {
    return Stream.of(
        Arguments.of(10, 1, "Processing reads: 10% [=---------](1/10)\r"),
        Arguments.of(10, 2, "Processing reads: 10% [=---------](1/10)\r"
                + "Processing reads: 20% [==--------](2/10)\r"),
        Arguments.of(730000, 73000, "Processing reads: 10% [=---------](73000/730000)\r"),
        Arguments.of(730000, 73001, "Processing reads: 10% [=---------](73000/730000)\r"),
        Arguments.of(730000, 73002, "Processing reads: 10% [=---------](73000/730000)\r"),
        Arguments.of(730000, 74000, "Processing reads: 10% [=---------](73000/730000)\r"),
        Arguments.of(730000, 7400, ""),
        Arguments.of(11, 11, "Processing reads: 18% [=---------](2/11)\r" +
            "Processing reads: 27% [==--------](3/11)\r" +
            "Processing reads: 36% [===-------](4/11)\r" +
            "Processing reads: 45% [====------](5/11)\r" +
            "Processing reads: 54% [=====-----](6/11)\r" +
            "Processing reads: 63% [======----](7/11)\r" +
            "Processing reads: 72% [=======---](8/11)\r" +
            "Processing reads: 81% [========--](9/11)\r" +
            "Processing reads: 90% [=========-](10/11)\r" +
            "Processing reads: 100% [==========](11/11)\r" + System.lineSeparator())
    );
  }
}
