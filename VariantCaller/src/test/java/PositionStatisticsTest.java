import com.epam.bioinf.variantcaller.caller.position.PositionStatistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionStatisticsTest {
  @Test
  public void alleleDataMustCorrectlyIncrementStrandCounts() {
    PositionStatistics PositionStatistics = new PositionStatistics();
    PositionStatistics.incrementStrandCount(false);
    assertEquals(1, PositionStatistics.getForwardStrandCount());
    assertEquals(0, PositionStatistics.getReversedStrandCount());
    PositionStatistics.incrementStrandCount(true);
    assertEquals(1, PositionStatistics.getForwardStrandCount());
    assertEquals(1, PositionStatistics.getReversedStrandCount());
  }

  @Test
  public void alleleDataMustCorrectlyCountTotalStrandValue() {
    PositionStatistics PositionStatistics = new PositionStatistics();
    PositionStatistics.incrementStrandCount(false);
    PositionStatistics.incrementStrandCount(false);
    PositionStatistics.incrementStrandCount(true);
    assertEquals(3, PositionStatistics.count());
  }
}
