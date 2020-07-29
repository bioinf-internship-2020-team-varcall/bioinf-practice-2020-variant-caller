import com.epam.bioinf.variantcaller.caller.AlleleCounter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlleleCounterTest {
  @Test
  public void alleleDataMustCorrectlyIncrementStrandCounts() {
    AlleleCounter alleleCounter = new AlleleCounter();
    alleleCounter.incrementStrandCount(false);
    assertEquals(1, alleleCounter.getForwardStrandCnt());
    assertEquals(0, alleleCounter.getReversedStrandCnt());
    alleleCounter.incrementStrandCount(true);
    assertEquals(1, alleleCounter.getForwardStrandCnt());
    assertEquals(1, alleleCounter.getReversedStrandCnt());
  }

  @Test
  public void alleleDataMustCorrectlyCountTotalStrandValue() {
    AlleleCounter alleleCounter = new AlleleCounter();
    alleleCounter.incrementStrandCount(false);
    alleleCounter.incrementStrandCount(false);
    alleleCounter.incrementStrandCount(true);
    assertEquals(3, alleleCounter.count());
  }
}
