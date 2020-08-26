import com.epam.bioinf.variantcaller.caller.position.PositionStatistics;
import com.epam.bioinf.variantcaller.caller.sample.SampleData;
import com.epam.bioinf.variantcaller.caller.variant.VariantInfo;
import htsjdk.variant.variantcontext.Allele;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleDataTest {

  @Test
  public void sampleDataAlleleCounterMustBeCreatedEvenIfAlleleNotExistsInInternalMap() {
    VariantInfo variantInfo = new VariantInfo("chr2", 1, Allele.create((byte) 'G', true));
    SampleData sampleData = new SampleData(variantInfo);
    assertEquals(new PositionStatistics().toString(),
        sampleData.computeAllele(Allele.create((byte) 'C', false)).toString());
  }
}
