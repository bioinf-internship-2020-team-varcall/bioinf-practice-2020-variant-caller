import com.epam.bioinf.variantcaller.caller.AlleleCounter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlleleCounterTest {
  @Test
  public void alleleDataMustCorrectlyIncrementStrandCounts() {
    AlleleCounter alleleCounter = new AlleleCounter();
    alleleCounter.incrementStrandCount(false);
    assertEquals(1, alleleCounter.countStrands[0]);
    assertEquals(0, alleleCounter.countStrands[1]);
    alleleCounter.incrementStrandCount(true);
    assertEquals(1, alleleCounter.countStrands[0]);
    assertEquals(1, alleleCounter.countStrands[1]);
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
