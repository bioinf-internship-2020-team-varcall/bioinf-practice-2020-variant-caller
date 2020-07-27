import com.epam.bioinf.variantcaller.caller.AlleleData;
import htsjdk.variant.variantcontext.Allele;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlleleDataTest {
  @Test
  public void alleleDataMustCorrectlyIncrementStrandCounts() {
    AlleleData alleleData = new AlleleData(Allele.create((byte) 'G', false));
    alleleData.incrementStrandCount(false);
    assertEquals(1, alleleData.countStrands[0]);
    assertEquals(0, alleleData.countStrands[1]);
    alleleData.incrementStrandCount(true);
    assertEquals(1, alleleData.countStrands[0]);
    assertEquals(1, alleleData.countStrands[1]);
  }

  @Test
  public void alleleDataMustCorrectlyCountTotalStrandValue() {
    AlleleData alleleData = new AlleleData(Allele.create((byte) 'G', false));
    alleleData.incrementStrandCount(false);
    alleleData.incrementStrandCount(false);
    alleleData.incrementStrandCount(true);
    assertEquals(3, alleleData.count());
  }
}
