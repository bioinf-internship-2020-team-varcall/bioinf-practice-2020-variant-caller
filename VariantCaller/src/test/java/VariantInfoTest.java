import com.epam.bioinf.variantcaller.caller.variant.VariantInfo;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VariantInfoTest {

  @Test
  public void contextWithoutSampleDataByGivenSampleNameWillCreateOne() {
    //Create an object which does not contain any sample data
    VariantInfo variantInfo = new VariantInfo("chr2", 1, Allele.create((byte) 'G', true));
    //Check that when trying to get sample data by sample
    // name variant info automatically creates one
    assertNotNull(variantInfo.computeSample("Hi,Mom!"));
  }

  @Test
  public void foundContextMustReturnCorrectAcAndAf() {
    VariantContext testedContext = getTestedVariantContextWithMultipleVariantsAndGenotypes();
    assertEquals(Arrays.asList(8, 4, 4, 7), testedContext.getAttributeAsIntList("AC", 0));
    assertEquals(Arrays.asList(0.348, 0.174, 0.174, 0.304),
        testedContext.getAttributeAsDoubleList("AF", 0.0));
  }

  @Test
  public void foundContextAlleleCountElementsSumMustBeEqualToAlleleNumber() {
    VariantContext testedContext = getTestedVariantContextWithMultipleVariantsAndGenotypes();
    assertEquals(23, testedContext.getAttributeAsIntList("AC", 0)
        .stream()
        .mapToInt(Integer::intValue).sum());
  }

  @Test
  public void foundContextMustReturnCorrectAltAlleles() {
    VariantContext testedContext = getTestedVariantContextWithMultipleVariantsAndGenotypes();
    assertEquals(
        Arrays.asList(
            Allele.create((byte) 'A', false),
            Allele.create((byte) 'C', false),
            Allele.create((byte) 'T', false)
        ),
        testedContext.getAlternateAlleles());
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectSampleNames() {
    VariantContext testedContext = getTestedVariantContextWithMultipleVariantsAndGenotypes();
    assertNotNull(testedContext.getGenotype("Hi,Mom!"));
    assertNotNull(testedContext.getGenotype("Bye,Mom!"));
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectListOfAllelesAndDpg() {
    VariantContext testedContext = getTestedVariantContextWithMultipleVariantsAndGenotypes();
    Genotype testedGenotype1 = testedContext.getGenotype("Hi,Mom!");
    Genotype testedGenotype2 = testedContext.getGenotype("Bye,Mom!");
    assertEquals(Arrays.asList(Allele.create((byte) 'A', false),
        Allele.create((byte) 'C', false),
        Allele.create((byte) 'T', false)),
        testedGenotype1.getAlleles());
    assertEquals(Arrays.asList(1, 3, 2), testedGenotype1.getAnyAttribute("DPG"));
    assertEquals(Arrays.asList(Allele.create((byte) 'G', true),
        Allele.create((byte) 'A', false),
        Allele.create((byte) 'C', false),
        Allele.create((byte) 'T', false)),
        testedGenotype2.getAlleles());
    assertEquals(Arrays.asList(8, 3, 1, 5), testedGenotype2.getAnyAttribute("DPG"));
  }

  @Test
  public void foundContextMustHoldGenotypesWithDpEqualToDpgElementsSum() {
    VariantContext testedContext = getTestedVariantContextWithMultipleVariantsAndGenotypes();
    Genotype testedGenotype1 = testedContext.getGenotype("Hi,Mom!");
    Genotype testedGenotype2 = testedContext.getGenotype("Bye,Mom!");
    //DPG is always able to be casted to a list of integer
    @SuppressWarnings("unchecked")
    List<Integer> testedDpg1 = (List<Integer>) testedGenotype1.getAnyAttribute("DPG");
    @SuppressWarnings("unchecked")
    List<Integer> testedDpg2 = (List<Integer>) testedGenotype2.getAnyAttribute("DPG");
    assertEquals(testedGenotype1.getDP(), testedDpg1.stream().mapToInt(Integer::intValue).sum());
    assertEquals(testedGenotype2.getDP(), testedDpg2.stream().mapToInt(Integer::intValue).sum());
  }

  private VariantContext getTestedVariantContextWithMultipleVariantsAndGenotypes() {
    VariantInfo variantInfo = new VariantInfo("chr2", 1, Allele.create((byte) 'G', true));
    incrementStrandCountBySampleName(variantInfo, "Hi,Mom!", 'C', false, 3);
    incrementStrandCountBySampleName(variantInfo, "Hi,Mom!", 'T', true, 2);
    incrementStrandCountBySampleName(variantInfo, "Hi,Mom!", 'A', false, 1);

    incrementStrandCountBySampleName(variantInfo, "Bye,Mom!", 'C', true, 1);
    incrementStrandCountBySampleName(variantInfo, "Bye,Mom!", 'T', false, 5);
    incrementStrandCountBySampleName(variantInfo, "Bye,Mom!", 'A', false, 3);
    incrementStrandCountBySampleName(variantInfo, "Bye,Mom!", 'G', true, 8);
    return variantInfo.makeVariantContext();
  }

  private void incrementStrandCountBySampleName(VariantInfo variantInfo,
                                                String sampleName,
                                                char altAllele,
                                                boolean isStrandReversed,
                                                int times) {
    for (int i = 0; i < times; i++) {
      variantInfo.computeSample(sampleName)
          .computeAllele(Allele.create((byte) altAllele, false))
          .incrementStrandCount(isStrandReversed);
    }
  }
}
