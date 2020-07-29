import com.epam.bioinf.variantcaller.caller.SampleData;
import com.epam.bioinf.variantcaller.caller.VariantInfo;
import htsjdk.variant.variantcontext.Allele;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SampleDataTest {

  @Test
  public void sampleDataAlleleCounterMustBeCreatedEvenIfAlleleNotExistsInInternalMap() {
    VariantInfo variantInfo = new VariantInfo("chr2", 1, Allele.create((byte) 'G', true));
    SampleData sampleData = new SampleData(variantInfo);
    assertNotNull(sampleData.getAllele(Allele.create((byte) 'C', false)));
  }
}
